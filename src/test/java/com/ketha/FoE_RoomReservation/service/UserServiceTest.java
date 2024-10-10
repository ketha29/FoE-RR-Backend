package com.ketha.FoE_RoomReservation.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ketha.FoE_RoomReservation.dto.LoginDto;
import com.ketha.FoE_RoomReservation.dto.ResponseDto;
import com.ketha.FoE_RoomReservation.model.User;
import com.ketha.FoE_RoomReservation.model.User.UserType;
import com.ketha.FoE_RoomReservation.repository.UserRepository;
import com.ketha.FoE_RoomReservation.security.CustomUserDetailsService;
import com.ketha.FoE_RoomReservation.security.JwtService;
import com.ketha.FoE_RoomReservation.service.impl.UserServiceImpl;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;
    
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private CustomUserDetailsService customUserDetailsService;
    
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private User admin;
    private User superAdmin;
    private User regularUser1;
    private User regularUser2;
    @BeforeEach
    public void setUp() {
		SecurityContextHolder.setContext(securityContext);
		
		user = User.builder()
				.firstName("David")
				.lastName("John")
				.email("Davidjohn123@gmail.com")
				.phoneNo(0771233456)
				.userName("David")
				.password("password")
				.userType(UserType.admin)
				.build();
		
		admin = User.builder()
				.firstName("David")
				.lastName("John")
				.email("Davidjohn123@gmail.com")
				.phoneNo(0771233456)
				.userName("David")
				.password("password")
				.userType(UserType.admin)
				.build();

		superAdmin = User.builder()
				.firstName("super")
				.lastName("Admin")
				.email("SuperAdmin@gmail.com")
				.phoneNo(0771564566)
				.userName("superAdminUser")
				.password("super123")
				.userType(UserType.superAdmin)
				.build();

		regularUser1 = User.builder()
				.firstName("Oliver")
				.lastName("Wilson")
				.email("oliver@gmail.com")
				.phoneNo(0771233456)
				.userName("e20199")
				.password("password")
				.userType(UserType.regularUser)
				.build();
			

		regularUser2 = User.builder()
				.firstName("David")
				.lastName("Miller")
				.email("Davidmiller123@gmail.com")
				.phoneNo(0771233456)
				.userName("Miller")
				.password("password")
				.userType(UserType.regularUser)
				.build();
    }

    @Test
    public void UserService_Register_ReturnUser() {
        // Arrange
        when(userRepository.existsByUserName(user.getUserName())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        ResponseDto response = userService.register(user);

        // Assert
		Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
        Assertions.assertThat(response.getMessage()).isEqualTo("User added successfully");
        Assertions.assertThat(response.getUser()).isNotNull();
    }

    @Test
    public void UserService_Register_UserAlreadyExists() {
        // Arrange
        when(userRepository.existsByUserName(user.getUserName())).thenReturn(true);

        // Act
        ResponseDto response = userService.register(user);

        // Assert
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(404);
        Assertions.assertThat(response.getMessage()).isEqualTo(user.getUserName() + " Already exists");
    }

    @Test
    public void UserService_Register_EmptyUsernameOrPassword() {
        // Arrange
        user.setUserName("");
        user.setPassword("");

        // Act
        ResponseDto response = userService.register(user);

        // Assert
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(400);
        Assertions.assertThat(response.getMessage()).isEqualTo("Bad request: User name or password shouldn't be empty");
    }
    
    @Test
    public void UserService_Login_ValidLogin() {
    	// Arrange
    	LoginDto loginDto = LoginDto.builder()
    						.userName(user.getUserName())
    						.password(user.getPassword())
    						.build();
    	
    	when(userRepository.findByUserName(loginDto.getUserName())).thenReturn(Optional.of(user));
    	when(jwtService.generateToken(customUserDetailsService.loadUserByUsername(user.getUserName()))).thenReturn("jwtToken");
    	
    	// Act
    	ResponseDto response = userService.login(loginDto);
    	
    	// Assert
    	Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
        Assertions.assertThat(response.getMessage()).isEqualTo("User login successful");
        Assertions.assertThat(response.getUserType()).isEqualTo(UserType.admin);
    }
    
    @Test
    public void UserService_Login_InValidLogin() {
    	// Arrange
    	LoginDto loginDto = LoginDto.builder()
    						.userName(user.getUserName())
    						.password("wrong_password")
    						.build();
    	
    	when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
    	
    	// Act
    	ResponseDto response = userService.login(loginDto);
    	
    	// Assert
    	Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(400);
    }
    
    @Test
    public void UserService_GetAllUsers_ReturnAdminAndRegularUsers() {
        // Arrange
    	when(securityContext.getAuthentication()).thenReturn(authentication);
    	when(authentication.getName()).thenReturn("superAdminUser");
    	when(userRepository.findByUserName("superAdminUser")).thenReturn(Optional.of(superAdmin));
    	when(userRepository.findUserByUserType(UserType.admin)).thenReturn(Arrays.asList(admin));
    	when(userRepository.findUserByUserType(UserType.regularUser)).thenReturn(Arrays.asList(regularUser1, regularUser2));
       
        // Act
        ResponseDto response = userService.getAllUsers();

        // Assert
		Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
        Assertions.assertThat(response.getMessage()).isEqualTo("Successsful");
        Assertions.assertThat(response.getUserList()).isNotNull();
        Assertions.assertThat(response.getUserList().size()).isEqualTo(3);
    }
    
    @Test
    public void UserService_GetAllUsers_ReturnRegularUsers() {
        // Arrange    	
    	when(securityContext.getAuthentication()).thenReturn(authentication);
    	when(authentication.getName()).thenReturn("admin");
    	when(userRepository.findByUserName("admin")).thenReturn(Optional.of(admin));
    	when(userRepository.findUserByUserType(UserType.regularUser)).thenReturn(Arrays.asList(regularUser1, regularUser2));
       
        // Act
        ResponseDto response = userService.getAllUsers();

        // Assert
		Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
        Assertions.assertThat(response.getMessage()).isEqualTo("Successsful");
        Assertions.assertThat(response.getUserList()).isNotNull();
        Assertions.assertThat(response.getUserList().size()).isEqualTo(2);
    }
    
    @Test
    public void UserService_GetAllUsers_Forbidden() {
        // Arrange    	
    	when(securityContext.getAuthentication()).thenReturn(authentication);
    	when(authentication.getName()).thenReturn("regularUser1");
    	when(userRepository.findByUserName("regularUser1")).thenReturn(Optional.of(regularUser1));
 
       
        // Act
        ResponseDto response = userService.getAllUsers();

        // Assert
		Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(403);
        Assertions.assertThat(response.getMessage()).isEqualTo("Permission not allowed: Forbidden");
        Assertions.assertThat(response.getUserList()).isNull();
    }
    
    @Test
    public void UserService_GetAllUsers_UserNotFound() {
        // Arrange    	
    	when(securityContext.getAuthentication()).thenReturn(authentication);
    	when(authentication.getName()).thenReturn("unknownUser");
    	when(userRepository.findByUserName("unknownUser")).thenReturn(Optional.empty());
 
       
        // Act
        ResponseDto response = userService.getAllUsers();

        // Assert
		Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(404);
        Assertions.assertThat(response.getMessage()).isEqualTo("User not found: NotFound");
        Assertions.assertThat(response.getUserList()).isNull();
    }
    
    @Test
    public void UserService_GetUserByIdByRegularUser_ReturnException() {
    	// Arrange
    	when(securityContext.getAuthentication()).thenReturn(authentication);
    	when(authentication.getName()).thenReturn("regularUser1");
    	when(userRepository.findByUserName("regularUser1")).thenReturn(Optional.of(regularUser1));
    	when(userRepository.findById((long) 1)).thenReturn(Optional.of(regularUser2));
    	
    	// Act
    	ResponseDto response = userService.getUserById(1);
    	
    	// Assert
    	Assertions.assertThat(response).isNotNull();
    	Assertions.assertThat(response.getStatusCode()).isEqualTo(404);
        Assertions.assertThat(response.getMessage()).isEqualTo("Permission not allowed: Forbidden" );
        Assertions.assertThat(response.getUserList()).isNull();
    }
    
    @Test
    public void UserService_GetRegularUserByIdByAdmin_ReturnRegularUser() {
    	// Arrange
    	when(securityContext.getAuthentication()).thenReturn(authentication);
    	when(authentication.getName()).thenReturn("admin");
    	when(userRepository.findByUserName("admin")).thenReturn(Optional.of(admin));
    	when(userRepository.findById((long) 1)).thenReturn(Optional.of(regularUser1));
    	
    	// Act
    	ResponseDto response = userService.getUserById(1);
    	
    	// Assert
    	Assertions.assertThat(response).isNotNull();
    	Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
        Assertions.assertThat(response.getMessage()).isEqualTo("Successful");
        Assertions.assertThat(response.getUser()).isNotNull();
    }
    
    @Test
    public void UserService_GetRegularUserByIdBySupAdmin_ReturnRegularUser() {
    	// Arrange
    	when(securityContext.getAuthentication()).thenReturn(authentication);
    	when(authentication.getName()).thenReturn("superAdmin");
    	when(userRepository.findByUserName("superAdmin")).thenReturn(Optional.of(superAdmin));
    	when(userRepository.findById((long) 1)).thenReturn(Optional.of(regularUser1));
    	
    	// Act
    	ResponseDto response = userService.getUserById(1);
    	
    	// Assert
    	Assertions.assertThat(response).isNotNull();
    	Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
        Assertions.assertThat(response.getMessage()).isEqualTo("Successful");
        Assertions.assertThat(response.getUser()).isNotNull();
    }
    
    @Test
    public void UserService_GetAdminUserByIdBySupAdmin_ReturnAdminUser() {
    	// Arrange
    	when(securityContext.getAuthentication()).thenReturn(authentication);
    	when(authentication.getName()).thenReturn("superAdmin");
    	when(userRepository.findByUserName("superAdmin")).thenReturn(Optional.of(superAdmin));
    	when(userRepository.findById((long) 1)).thenReturn(Optional.of(admin));
    	
    	// Act
    	ResponseDto response = userService.getUserById(1);
    	
    	// Assert
    	Assertions.assertThat(response).isNotNull();
    	Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
        Assertions.assertThat(response.getMessage()).isEqualTo("Successful");
        Assertions.assertThat(response.getUser()).isNotNull();
    }
    
    @Test
    public void UserService_GetAdminUserByIdByAdminUser_ReturnException() {
    	// Arrange
    	when(securityContext.getAuthentication()).thenReturn(authentication);
    	when(authentication.getName()).thenReturn("admin");
    	when(userRepository.findByUserName("admin")).thenReturn(Optional.of(admin));
    	when(userRepository.findById((long) 1)).thenReturn(Optional.of(user));
    	
    	// Act
    	ResponseDto response = userService.getUserById(1);
    	
    	// Assert
    	Assertions.assertThat(response).isNotNull();
    	Assertions.assertThat(response.getStatusCode()).isEqualTo(404);
        Assertions.assertThat(response.getMessage()).isEqualTo("Permission not allowed: Forbidden" );
        Assertions.assertThat(response.getUserList()).isNull();
    }
    
    @Test
    public void UserService_UserIdNotFound_ReturnException() {
    	// Arrange
    	when(securityContext.getAuthentication()).thenReturn(authentication);
    	when(authentication.getName()).thenReturn("admin");
    	when(userRepository.findByUserName("admin")).thenReturn(Optional.of(admin));
    	when(userRepository.findById((long) 1)).thenReturn(Optional.empty());
    	
    	// Act
    	ResponseDto response = userService.getUserById(1);
    	
    	// Assert
    	Assertions.assertThat(response).isNotNull();
    	Assertions.assertThat(response.getStatusCode()).isEqualTo(404);
        Assertions.assertThat(response.getMessage()).isEqualTo("User not found: NotFound");
        Assertions.assertThat(response.getUser()).isNull();
    }
    
//    @Test
//    public void UserService_DeleteUser_ReturnResponse() {
//    	// Arrange
//    	when(securityContext.getAuthentication()).thenReturn(authentication);
//    	when(authentication.getName()).thenReturn("admin");
//    	when(userRepository.findByUserName("admin")).thenReturn(Optional.of(admin));
//    	when(userRepository.findById((long) 1)).thenReturn(Optional.empty());
//    	
//
//    	// Act
//    	ResponseDto response = userService.deleteUser(1);
//    	
//    	// Assert
//    	Assertions.assertThat(response).isNotNull();
//    	Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
//        Assertions.assertThat(response.getMessage()).isEqualTo("Successful");
//        Assertions.assertThat(response.getUser()).isNull();
//    }
    
    @Test
    public void UserService_GetUserBookingsByAdmin_ReturnBookings() {
    	// Arrange
    	when(securityContext.getAuthentication()).thenReturn(authentication);
    	when(authentication.getName()).thenReturn("admin");
    	when(userRepository.findByUserName("admin")).thenReturn(Optional.of(admin));
    	when(userRepository.findById((long) 1)).thenReturn(Optional.of(regularUser1));
    	
    	// Act
    	ResponseDto response = userService.getUserBookings(1);
    	
    	// Assert
    	Assertions.assertThat(response).isNotNull();
    	Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
        Assertions.assertThat(response.getMessage()).isEqualTo("Successful");
        Assertions.assertThat(response.getUser()).isNotNull();
    }
    
    @Test
    public void UserService_GetUserBookingsBySuperAdmin_ReturnBookings() {
    	// Arrange
    	when(securityContext.getAuthentication()).thenReturn(authentication);
    	when(authentication.getName()).thenReturn("superAdmin");
    	when(userRepository.findByUserName("superAdmin")).thenReturn(Optional.of(superAdmin));
    	when(userRepository.findById((long) 1)).thenReturn(Optional.of(regularUser1));
    	
    	// Act
    	ResponseDto response = userService.getUserBookings(1);
    	
    	// Assert
    	Assertions.assertThat(response).isNotNull();
    	Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
        Assertions.assertThat(response.getMessage()).isEqualTo("Successful");
        Assertions.assertThat(response.getUser()).isNotNull();
    }
    
    @Test
    public void UserService_GetAdminBookingsBySuperAdmin_ReturnBookings() {
    	// Arrange
    	when(securityContext.getAuthentication()).thenReturn(authentication);
    	when(authentication.getName()).thenReturn("superAdmin");
    	when(userRepository.findByUserName("superAdmin")).thenReturn(Optional.of(superAdmin));
    	when(userRepository.findById((long) 1)).thenReturn(Optional.of(admin));
    	
    	// Act
    	ResponseDto response = userService.getUserBookings(1);
    	
    	// Assert
    	Assertions.assertThat(response).isNotNull();
    	Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
        Assertions.assertThat(response.getMessage()).isEqualTo("Successful");
        Assertions.assertThat(response.getUser()).isNotNull();
    }
    
    @Test
    public void UserService_GetUserBookingsByRegularUser_ReturnException() {
    	// Arrange
    	when(securityContext.getAuthentication()).thenReturn(authentication);
    	when(authentication.getName()).thenReturn("user");
    	when(userRepository.findByUserName("user")).thenReturn(Optional.of(regularUser1));
    	when(userRepository.findById((long) 1)).thenReturn(Optional.of(regularUser2));
    	
    	// Act
    	ResponseDto response = userService.getUserBookings(1);
    	
    	// Assert
    	Assertions.assertThat(response).isNotNull();
    	Assertions.assertThat(response.getStatusCode()).isEqualTo(404);
        Assertions.assertThat(response.getMessage()).isEqualTo("Permission not allowed: Forbidden");
        Assertions.assertThat(response.getUser()).isNull();
    }
    
    @Test
    public void UserService_UserIdNotFoundInGetBooking_ReturnException() {
    	// Arrange
    	when(securityContext.getAuthentication()).thenReturn(authentication);
    	when(authentication.getName()).thenReturn("admin");
    	when(userRepository.findByUserName("admin")).thenReturn(Optional.of(admin));
    	when(userRepository.findById((long) 1)).thenReturn(Optional.empty());
    	
    	// Act
    	ResponseDto response = userService.getUserBookings(1);
    	
    	// Assert
    	Assertions.assertThat(response).isNotNull();
    	Assertions.assertThat(response.getStatusCode()).isEqualTo(404);
        Assertions.assertThat(response.getMessage()).isEqualTo("User not found: NotFound");
        Assertions.assertThat(response.getUser()).isNull();
    }
}
