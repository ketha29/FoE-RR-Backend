package com.ketha.FoE_RoomReservation.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ketha.FoE_RoomReservation.dto.ResponseDto;
import com.ketha.FoE_RoomReservation.dto.UserDto;
import com.ketha.FoE_RoomReservation.service.impl.JwtTokenProvider;
import com.ketha.FoE_RoomReservation.service.impl.UserServiceImpl;

import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin("5173")
@RestController
@RequestMapping("/auth")
public class AuthController {

	private UserServiceImpl userService;
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	public AuthController(UserServiceImpl userService, JwtTokenProvider jwtTokenProvider) {
		this.userService = userService;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@GetMapping("/login")
	public ResponseEntity<ResponseDto> login(Authentication authentication) {
		ResponseDto response = userService.login(authentication);
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}

	@GetMapping("/me")
	public ResponseEntity<ResponseDto> getCurrentUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
		// Verify the token is present
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String token = authHeader.substring(7); // Remove the bearer part

		// Extract user information from JWT
		String email = jwtTokenProvider.getUsernameFromToken(token);
		return ResponseEntity.status(200).body(userService.getUserByEmail(email));
	}

	@GetMapping("/refresh")
	public ResponseEntity<ResponseDto> refresh(@CookieValue(name = "refreshToken", required = false) String oldRefreshToken,
			HttpServletResponse response) {
		
		// If refresh token is not available just pass it
		if(oldRefreshToken==null) {
			return ResponseEntity.ok().build();
		}
		
		// Validate the refresh token
		if (!jwtTokenProvider.validateToken(oldRefreshToken)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		// Get the user from token
		String username = jwtTokenProvider.getUsernameFromToken(oldRefreshToken);
		UserDto user = userService.getUserByEmail(username).getUser();
		
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
				user.getEmail(), 
				null, 
				Collections.singletonList(new SimpleGrantedAuthority(user.getUserType().toString())));
		
		SecurityContextHolder.getContext().setAuthentication(authToken);
		
		// Generate new tokens
		String accessToken = jwtTokenProvider.generateAccessToken(user);
		String refreshToken = jwtTokenProvider.generateRefreshToken(user);

		// Set the cookie
		ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken).httpOnly(true).secure(true)
				.path("/auth/refresh").maxAge(jwtTokenProvider.getRefreshTokenExpiration() / 1000).sameSite("Lax")
				.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
		return ResponseEntity.ok(ResponseDto.builder().token(accessToken).build());
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken,
			HttpServletResponse response) {
		
		// Immediately invalidate the cookie
		ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
				.httpOnly(false)
				.secure(true)
				.path("/auth/refresh")
				.maxAge(0)
				.sameSite("Lax")
				.build();
		response.addHeader(HttpHeaders.SET_COOKIE,cookie.toString());
		System.err.println("Logged out");
		return ResponseEntity.noContent().build();
	}
}