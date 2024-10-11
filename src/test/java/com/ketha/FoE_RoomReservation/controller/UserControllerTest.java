package com.ketha.FoE_RoomReservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ketha.FoE_RoomReservation.dto.ResponseDto;
import com.ketha.FoE_RoomReservation.dto.UserDto;
import com.ketha.FoE_RoomReservation.model.User;
import com.ketha.FoE_RoomReservation.model.User.UserType;
import com.ketha.FoE_RoomReservation.service.impl.UserServiceImpl;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

	@Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;
    
    @Autowired
    private ObjectMapper objectMapper;
    private User user;
    private UserDto userDto;
    private ResponseDto responseDto;
    
    @BeforeEach
    public void init() {
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        user = User.builder()
        		.userId(1)
                .email("e20199@eng.pdn.ac.lk")
                .phoneNo(0771233456)
                .userName("e20199")
                .password("password")
                .userType(UserType.admin)
                .build();
        
        userDto = UserDto.builder()
        		.userId(1)
                .email("e20199@eng.pdn.ac.lk")
                .phoneNo(0771233456)
                .userName("e20199")
                .userType(UserType.admin)
                .build();
        
        responseDto = ResponseDto.builder()
        			.message("Successful")
        			.user(userDto)
        			.StatusCode(200)
        			.build();
    }
    
    @Test
    public void UserController_Register_ReturnUserType() throws Exception {
        // Mocking the service behavior
        given(userService.register(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        // Performing an HTTP POST request to register a user
    	ResultActions response = mockMvc.perform(post("/user/register")
    	        .contentType(MediaType.APPLICATION_JSON)
    	        .content(objectMapper.writeValueAsString(user)));

        // Asserting the response expectations
        response.andExpect(MockMvcResultMatchers.status().isCreated())
        		.andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(user.getEmail())))
        		.andExpect(MockMvcResultMatchers.jsonPath("$.phoneNo", CoreMatchers.is(user.getPhoneNo())))
        		.andExpect(MockMvcResultMatchers.jsonPath("$.userName", CoreMatchers.is(user.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userType", CoreMatchers.is(user.getUserType().toString())));
    }
    
    @Test
    public void UserController_GetUserById_ReturnUser() throws Exception {
        // Define the employee ID for the test
        int userId = 1;

        // Mocking the service behavior to return an Optional containing a specific Employee instance
        when(userService.getUserById(userId)).thenReturn(responseDto);

        // Performing an HTTP GET request to retrieve an employee by ID
        ResultActions response = mockMvc.perform(get("/api/employee/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(responseDto)));

        // Asserting the response expectations
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is(responseDto.getMessage())));
//                .andExpect(MockMvcResultMatchers.jsonPath("$.department", CoreMatchers.is(employee.getDepartment())));
    }
}
