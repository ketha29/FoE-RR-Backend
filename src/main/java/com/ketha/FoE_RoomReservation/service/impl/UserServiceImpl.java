package com.ketha.FoE_RoomReservation.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.ketha.FoE_RoomReservation.dto.ResponseDto;
import com.ketha.FoE_RoomReservation.dto.UserDto;
import com.ketha.FoE_RoomReservation.exception.BadRequestException;
import com.ketha.FoE_RoomReservation.exception.CustomException;
import com.ketha.FoE_RoomReservation.exception.ForbiddenException;
import com.ketha.FoE_RoomReservation.model.User;
import com.ketha.FoE_RoomReservation.model.User.UserType;
import com.ketha.FoE_RoomReservation.repository.UserRepository;
import com.ketha.FoE_RoomReservation.service.interfac.UserService;
import com.ketha.FoE_RoomReservation.utils.Utils;

@Service
public class UserServiceImpl implements UserService {

	private UserRepository userRepository;

	// Setter for dependency injection
	// UserService has its UserRepository dependency set at the time of creation
	@Autowired
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	// Register user
	@Override
	// TODO super admin can assign admins but admin can simply assign regular user
	public ResponseDto register(User user) {
		ResponseDto response = new ResponseDto();
		try {
			if (user.getUserType() == null) {
				user.setUserType(UserType.regularUser);
			}
			if (userRepository.existsByEmail(user.getEmail())) {
				throw new CustomException(user.getEmail() + " Already exists");
			}
			User savedUser = userRepository.save(user);
			UserDto userDto = Utils.mapUserToUserDto(savedUser);
			response.setStatusCode(200);
			response.setMessage("User added successfully");
			response.setUser(userDto);
		} catch (CustomException e) {
			response.setStatusCode(404);
			response.setMessage(e.getMessage());
		} catch (BadRequestException e) {
			response.setStatusCode(400);
			response.setMessage("Bad request: " + e.getMessage());
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error occurred during User registration: " + e.getMessage());
		}
		return response;
	}

//	 User login
	@Override
	public ResponseDto login(Authentication authentication) {
		ResponseDto response = new ResponseDto();

		try {
			OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
			var user = userRepository.findByEmail(oAuth2User.getAttribute("email"))
					.orElseThrow(() -> new BadCredentialsException(null));

			if (authentication != null && authentication instanceof OAuth2AuthenticationToken) {
				DefaultOidcUser principal = (DefaultOidcUser) authentication.getPrincipal();
				response.setToken(principal.getAccessTokenHash());
			}
			response.setUserType(user.getUserType());
			response.setStatusCode(200);
			response.setMessage("User login successful");
			response.setUserId(user.getUserId());

		} catch (BadCredentialsException e) {
			response.setStatusCode(400);
			response.setMessage("Invalid login credentials: " + e.getMessage());
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error occurred during User login: " + e.getMessage());
		}
		return response;
	}

	// Get all users
	@Override
	public ResponseDto getAllUsers() {
		ResponseDto response = new ResponseDto();

		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication instanceof UsernamePasswordAuthenticationToken) {

				User loginUser = userRepository.findByEmail(authentication.getPrincipal().toString())
						.orElseThrow(() -> new CustomException("NotFound"));

				List<User> userList = null;
				if (loginUser.getUserType().equals(UserType.admin)) {
					userList = userRepository.findUserByUserType(UserType.regularUser);
				} else if (loginUser.getUserType().equals(UserType.superAdmin)) {
					userList = userRepository.findUserByUserType(UserType.admin);
					List<User> regularUsers = userRepository.findUserByUserType(UserType.regularUser);
					userList = Stream.concat(userList.stream(), regularUsers.stream()).collect(Collectors.toList());
				} else {
					userList = userRepository.findUserByUserType(UserType.regularUser);
				}

				List<UserDto> userDto = userList.stream().map((user) -> Utils.mapUserToUserDto(user))
						.collect(Collectors.toList());
				response.setUserList(userDto);
				response.setStatusCode(200);
				response.setMessage("Successsful");

			} else {
				throw new CustomException("failed authorization");
			}
		} catch (CustomException e) {
			response.setStatusCode(404);
			response.setMessage("User not found: " + e.getMessage());
		} catch (ForbiddenException e) {
			response.setStatusCode(403);
			response.setMessage("Permission not allowed: " + e.getMessage());
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error getting all users: " + e.getMessage());
		}
		return response;
	}

	// Get a user using the userId
	@Override
	public ResponseDto getUserById(long userId) {
		ResponseDto response = new ResponseDto();

		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication instanceof UsernamePasswordAuthenticationToken) {
				
				User loginUser = userRepository.findByEmail(authentication.getPrincipal().toString())
						.orElseThrow(() -> new CustomException("NotFound"));

				User user = userRepository.findById(userId).orElseThrow(() -> new CustomException("NotFound"));
				UserDto userDto;
				if ((loginUser.getUserType().equals(UserType.admin) && user.getUserType().equals(UserType.regularUser))
						|| (loginUser.getUserType().equals(UserType.superAdmin)
								&& ((user.getUserType().equals(UserType.regularUser))
										|| (user.getUserType().equals(UserType.admin))))) {
					userDto = Utils.mapUserToUserDto(user);
					response.setStatusCode(200);
					response.setMessage("Successful");
					response.setUser(userDto);
				} else {
					throw new ForbiddenException("Forbidden");
				}
			} else {
				throw new CustomException("failed authorization");
			}

		} catch (CustomException e) {
			response.setStatusCode(404);
			response.setMessage("User not found: " + e.getMessage());
		} catch (ForbiddenException e) {
			response.setStatusCode(404);
			response.setMessage("Permission not allowed: " + e.getMessage());
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error getting the user: " + e.getMessage());
		}
		return response;
	}

	public ResponseDto getLoggedUser() {
		ResponseDto response = new ResponseDto();
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication instanceof UsernamePasswordAuthenticationToken) {
				
				User loginUser = userRepository.findByEmail(authentication.getPrincipal().toString())
						.orElseThrow(() -> new CustomException("NotFound"));

				response.setUserId(loginUser.getUserId());
				response.setStatusCode(200);
				response.setMessage("Successful");
			}
		} catch (Exception e) {
			response.setStatusCode(401);
		}
		return response;
	}

	public ResponseDto getUserByEmail(String email) {
		ResponseDto response = new ResponseDto();
		try {

			User loginUser = userRepository.findByEmail(email).orElseThrow(() -> new CustomException("NotFound"));

			response.setUser(Utils.mapUserToUserDto(loginUser));
			response.setStatusCode(200);
			response.setMessage("Successful");
		} catch (Exception e) {
			response.setStatusCode(401);
			response.setMessage("Unauthorized");
		}
		return response;
	}

	@Override
	public ResponseDto getUserbyFullName(String fullName) {
		ResponseDto response = new ResponseDto();

		String[] names = fullName.trim().split("\\s+");
		String regexPattern = names[0];
		regexPattern = names.length > 1 ? names[1] : String.join("|", names); // Handling null LastName
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication instanceof UsernamePasswordAuthenticationToken) {
				
				User loginUser = userRepository.findByEmail(authentication.getPrincipal().toString())
						.orElseThrow(() -> new CustomException("Not Found"));

				List<User> userList = null;
				if (loginUser.getUserType().equals(UserType.admin)
						|| loginUser.getUserType().equals(UserType.superAdmin)) {
					userList = userRepository.findByName(regexPattern);
					List<UserDto> userDto = userList.stream().filter(user -> user.getUserType() == UserType.regularUser)
							.map((user) -> Utils.mapUserToUserDto(user)).collect(Collectors.toList());
					response.setStatusCode(200);
					response.setMessage("Successful");
					response.setUserList(userDto);
				} else {
					throw new ForbiddenException("Forbidden");
				}
			} else {
				throw new CustomException("failed authorization");
			}

		} catch (CustomException e) {
			response.setStatusCode(404);
			response.setMessage("User not found: " + e.getMessage());
		} catch (ForbiddenException e) {
			response.setStatusCode(404);
			response.setMessage("Permission not allowed: " + e.getMessage());
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error getting the user: " + e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseDto getUserBookings(long userId) {
		ResponseDto response = new ResponseDto();

		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication instanceof UsernamePasswordAuthenticationToken) {

				User loginUser = userRepository.findByEmail(authentication.getPrincipal().toString())
						.orElseThrow(() -> new CustomException("NotFound"));

				User user = userRepository.findById(userId).orElseThrow(() -> new CustomException("NotFound"));
				UserDto userDto;
				if ((loginUser.getUserType().equals(UserType.admin) && user.getUserType().equals(UserType.regularUser))
						|| (loginUser.getUserType().equals(UserType.superAdmin)
								&& ((user.getUserType().equals(UserType.regularUser))
										|| (user.getUserType().equals(UserType.admin))))) {
					userDto = Utils.mapUserToUserEntityPlusBookings(user);
					response.setStatusCode(200);
					response.setMessage("Successful");
					response.setUser(userDto);
				} else {
					throw new ForbiddenException("Forbidden");
				}
			} else {
				throw new CustomException("failed authorization");
			}

		} catch (CustomException e) {
			response.setStatusCode(404);
			response.setMessage("User not found: " + e.getMessage());
		} catch (ForbiddenException e) {
			response.setStatusCode(404);
			response.setMessage("Permission not allowed: " + e.getMessage());
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error getting the user: " + e.getMessage());
		}
		return response;
	}

	// Update the details of an existing user object
//	public void updateUser(User user) {
//		repository.save(user);
//	}

	// Find the user object using the userId, and delete that user object
	@Override
	public ResponseDto deleteUser(long userId) {
		ResponseDto response = new ResponseDto();
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication instanceof UsernamePasswordAuthenticationToken) {

				User loginUser = userRepository.findByEmail(authentication.getPrincipal().toString())
						.orElseThrow(() -> new CustomException("NotFound"));
				if (loginUser.getUserType().equals(UserType.admin)
						|| loginUser.getUserType().equals(UserType.superAdmin)) {
					userRepository.findById(userId).orElseThrow(() -> new CustomException("User not found"));
					userRepository.deleteById(userId);
					response.setStatusCode(200);
					response.setMessage("Successful");
				}

			} else {
				throw new CustomException("failed authorization");
			}
		} catch (CustomException e) {
			response.setStatusCode(404);
			response.setMessage(e.getMessage());
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error in getting user: " + e.getMessage());
		}
		return response;
	}

}