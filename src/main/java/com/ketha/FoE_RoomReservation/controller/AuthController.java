package com.ketha.FoE_RoomReservation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ketha.FoE_RoomReservation.dto.LoginDto;
import com.ketha.FoE_RoomReservation.dto.ResponseDto;
import com.ketha.FoE_RoomReservation.service.impl.UserServiceImpl;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private UserServiceImpl userService;

	@Autowired
	public AuthController(UserServiceImpl userService) {
		this.userService = userService;
	}
	
	@PostMapping("/login")
	public ResponseEntity<ResponseDto> login(@RequestBody LoginDto loginDto) {
		ResponseDto response = userService.login(loginDto);
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}
}