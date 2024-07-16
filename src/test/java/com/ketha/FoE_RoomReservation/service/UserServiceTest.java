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

    @InjectMocks
    private UserServiceImpl userService;

    private User user = User.builder()
                .email("e20199@eng.pdn.ac.lk")
                .phoneNo(0771233456)
                .userName("e20199")
                .password("password")
                .userType(UserType.admin)
                .build();
  
//    private User admin;
//    private User superAdmin;
//    private User regularUser1;
//    private User regularUser2;
    private User admin = User.builder()
    					.userName("adminUser")
    					.userType(UserType.admin)
    					.build();
    
    private User superAdmin = User.builder()
							.userName("superAdminUser")
							.userType(UserType.superAdmin)
							.build();
    
    private User regularUser1 = User.builder()
							.userName("regularUser1")
							.userType(UserType.regularUser)
							.build();
    
    private User regularUser2 = User.builder()
							.userName("regularUser2")
							.userType(UserType.regularUser)
							.build();
    
    @BeforeEach
    public void setUp() {
		SecurityContextHolder.setContext(securityContext);
		
		
//		User admin = User.builder()
//					.userName("adminUser")
//					.userType(UserType.admin)
//					.build();
//
//		User superAdmin = User.builder()
//						.userName("superAdminUser")
//						.userType(UserType.superAdmin)
//						.build();
//
//		User regularUser1 = User.builder()
//							.userName("regularUser1")
//							.userType(UserType.regularUser)
//							.build();
//
//		User regularUser2 = User.builder()
//							.userName("regularUser2")
//							.userType(UserType.regularUser)
//							.build();
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

    	when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
    	when(userRepository.findByUserName(loginDto.getUserName())).thenReturn(Optional.of(user));
    	
    	// Act
    	ResponseDto response = userService.login(loginDto);
    	
    	// Assert
    	Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
        Assertions.assertThat(response.getMessage()).isEqualTo("User login successful");
        Assertions.assertThat(response.getUserType()).isEqualTo(UserType.admin);
    }
    
//    @Test
    // TODO unit test for invalid login
//    public void UserService_Login_InValidLogin() {
//    	// Arrange
//    	LoginDto loginDto = LoginDto.builder()
//    						.userName(user.getUserName())
//    						.password("wrong password")
//    						.build();
//
//    	when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
//    	when(userRepository.findByUserName(loginDto.getUserName())).thenReturn(Optional.of(user));
//    	
//    	// Act
//    	ResponseDto response = userService.login(loginDto);
//    	
//    	// Assert
//    	Assertions.assertThat(response).isNotNull();
//        Assertions.assertThat(response.getStatusCode()).isEqualTo(400);
//        Assertions.assertThat(response.getMessage()).isEqualTo("Invalid credentials: Invalid credentials");
//    }
    
    @Test
    public void UserService_GetAllUsers_ReturnAdminAndRegularUsers() {
        // Arrange
    	when(securityContext.getAuthentication()).thenReturn(authentication);
    	when(authentication.getName()).thenReturn("superAdminUser");
    	when(userRepository.findByUserName("superAdminUser")).thenReturn(Optional.of(superAdmin));
    	when(userRepository.findUserByUserType(UserType.admin)).thenReturn(Arrays.asList(admin));
    	// TODO fix to return regular users
//    	when(userRepository.findUserByUserType(UserType.regularUser)).thenReturn(Arrays.asList(regularUser1, regularUser2));
       
        // Act
        ResponseDto response = userService.getAllUsers();

        // Assert
		Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
        Assertions.assertThat(response.getMessage()).isEqualTo("Successsful");
        Assertions.assertThat(response.getUserList()).isNotNull();
        Assertions.assertThat(response.getUserList().size()).isEqualTo(1);
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
}
