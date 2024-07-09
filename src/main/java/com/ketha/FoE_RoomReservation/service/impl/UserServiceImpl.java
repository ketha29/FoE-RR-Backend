package com.ketha.FoE_RoomReservation.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ketha.FoE_RoomReservation.dto.LoginDto;
import com.ketha.FoE_RoomReservation.dto.ResponseDto;
import com.ketha.FoE_RoomReservation.dto.UserDto;
import com.ketha.FoE_RoomReservation.exception.CustomException;
import com.ketha.FoE_RoomReservation.model.User;
import com.ketha.FoE_RoomReservation.model.User.UserType;
import com.ketha.FoE_RoomReservation.repository.UserRepository;
import com.ketha.FoE_RoomReservation.service.interfac.UserService;
import com.ketha.FoE_RoomReservation.utils.Utils;


@Service
public class UserServiceImpl implements UserService{
	
	private UserRepository repository;
	private PasswordEncoder passwordEncoder;
	private AuthenticationManager authenticationManager;
	
	// Setter for dependency injection
	// UserService has its UserRepository dependency set at the time of creation
	@Autowired
	public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
	}
	
	// Register user
	@Override
	public ResponseDto register(User user) {
		ResponseDto response = new ResponseDto();
		try {
			if(user.getUserType() == null) {
				user.setUserType(UserType.regularUser);
			}
			if (repository.existsByUserName(user.getUserName())) {
				throw new CustomException(user.getUserName() + " Already exists");
			}
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			User savedUser = repository.save(user);
			UserDto userDto = Utils.mapUserEntityToUserDto(savedUser);
			response.setStatusCode(200);
			response.setMessage("Successsful");
			response.setUserDto(userDto);
		} catch (CustomException e) {
			response.setStatusCode(404);
            response.setMessage(e.getMessage());
		} catch (Exception e) {
			response.setStatusCode(500);
            response.setMessage("Error occurred during User registration " + e.getMessage());
		}
		return response;
	}
	
	// User login
	@Override
	public ResponseDto login(LoginDto loginDto) {
		ResponseDto response = new ResponseDto();
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUserName(), loginDto.getPassword()));
			User user = repository.findByUserName(loginDto.getUserName()).orElseThrow(() -> new BadCredentialsException(null));
			response.setUserType(user.getUserType());
			response.setStatusCode(200);
			response.setMessage("User login successful");
		} catch (BadCredentialsException e) {
	        response.setStatusCode(401);
	        response.setMessage("Invalid credentials: " + e.getMessage());
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
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String userName = auth.getName();
			User loginUser = repository.findByUserName(userName).orElseThrow(() -> new NotFoundException());
			if(loginUser.getUserType().equals(UserType.admin)) {
				List<User> users = repository.findUserByUserType(UserType.regularUser);
				List<UserDto> userDto = users.stream().map((user) -> Utils.mapUserEntityToUserDto(user)).collect(Collectors.toList());
				response.setUserList(userDto);
				response.setStatusCode(200);
				response.setMessage("Successsful");
			}
			else if(loginUser.getUserType().equals(UserType.superAdmin)) {
				List<User> users = repository.findUserByUserType(UserType.admin);
				users.addAll(repository.findUserByUserType(UserType.regularUser));
				List<UserDto> userDto = users.stream().map((user) -> Utils.mapUserEntityToUserDto(user)).collect(Collectors.toList());
				response.setUserList(userDto);
				response.setStatusCode(200);
				response.setMessage("Successsful");
			}
			else {
				response.setStatusCode(403);
				response.setMessage("Premission not allowed");
			}
			
			} catch(NotFoundException e) {
				response.setStatusCode(404);
				response.setMessage("User not found: " + e.getMessage());
			} catch(Exception e) {
				response.setStatusCode(500);
				response.setMessage("Error getting all users: " + e.getMessage());
			}
			return response;
	}

	// Get a user using the userId
	@Override
	public ResponseDto getUserById(int userId) {
		ResponseDto response = new ResponseDto();
		try {
			User user = repository.findById(userId).orElseThrow(() -> new CustomException("User not found"));
			UserDto userDto = Utils.mapUserEntityToUserDto(user);
			response.setStatusCode(200);
			response.setMessage("Successful");
			response.setUserDto(userDto);
		} catch(CustomException e) {
			response.setStatusCode(404);
			response.setMessage(e.getMessage());
		} catch(Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error in getting user: " + e.getMessage());
		}
		return response;
	}
	
	
//	// Update the details of an existing user object
//	public void updateUser(User user) {
//		repository.save(user);
//	}

	// Find the user object using the userId, and delete that user object
	@Override
	public ResponseDto deleteUser(int userId) {
		ResponseDto response = new ResponseDto();
		try {
			repository.findById(userId).orElseThrow(() -> new CustomException("User not found"));
			repository.deleteById(userId);
			response.setStatusCode(200);
			response.setMessage("Successful");
		} catch(CustomException e) {
			response.setStatusCode(404);
			response.setMessage(e.getMessage());
		} catch(Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error in getting user: " + e.getMessage());
		}
		return response;
	}
}