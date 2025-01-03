package com.ketha.FoE_RoomReservation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ketha.FoE_RoomReservation.dto.ResponseDto;
import com.ketha.FoE_RoomReservation.service.impl.UserServiceImpl;

@CrossOrigin("5173")
@RestController
@RequestMapping("/auth")
public class AuthController {

	private UserServiceImpl userService;

	@Autowired
	public AuthController(UserServiceImpl userService) {
		this.userService = userService;
	}

	@GetMapping("/login")
	public ResponseEntity<ResponseDto> login(Authentication authentication) {
		ResponseDto response = userService.login(authentication);
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}

	@GetMapping("/current-user")
	public String currentUser(Authentication authentication) {
		if (authentication != null) {
			return "User grants: " + (SecurityContextHolder.getContext().getAuthentication().getAuthorities());
		} else {
			return "No user is logged in";
		}
	}
	
	@GetMapping("/userinfo")
    public String userinfo(Authentication authentication) {
		OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
		oauth2Token.setAuthenticated(false);
		return oauth2Token.toString();
		
    }
}