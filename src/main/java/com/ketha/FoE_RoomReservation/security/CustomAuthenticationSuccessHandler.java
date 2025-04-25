package com.ketha.FoE_RoomReservation.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.ketha.FoE_RoomReservation.exception.CustomException;
import com.ketha.FoE_RoomReservation.model.User;
import com.ketha.FoE_RoomReservation.repository.UserRepository;
import com.ketha.FoE_RoomReservation.service.impl.JwtTokenProvider;
import com.ketha.FoE_RoomReservation.utils.Utils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Value("${frontend.url}")
	private String frontEndUrl;

	private UserRepository userRepository;
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	CustomAuthenticationSuccessHandler(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
		this.userRepository = userRepository;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		String email = oAuth2User.getAttribute("email");
		if (userRepository.existsByEmail(email)) {
			// Load the user
			User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException("User not found"));

//			// Grant authority
//			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
//			oAuth2User.getAuthorities().forEach(ga -> authorities.add(ga));
//			user.getAuthorities().forEach(ga -> authorities.add(ga));
//
//			OAuth2AuthenticationToken updatedAuthentication = new OAuth2AuthenticationToken(oAuth2User, authorities,
//					"sub");
//			SecurityContextHolder.getContext().setAuthentication(updatedAuthentication);

			// Remove the OAuthUser
			SecurityContextHolder.clearContext();
			
			// Generate tokens
			String accessToken = jwtTokenProvider.generateAccessToken(Utils.mapUserToUserDto(user));
			String refreshToken = jwtTokenProvider.generateRefreshToken(Utils.mapUserToUserDto(user));
			
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user.getEmail(),null,user.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authToken);
			
			// Set refresh token as HttpOnly cookie
			ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
					.httpOnly(true)
					.secure(true)
					.path("/auth/refresh")
					.maxAge(jwtTokenProvider.getRefreshTokenExpiration() / 1000)
					.sameSite("Lax")
					.build();

			response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

			// Redirect to frontend with access token
			String redirectUrl = frontEndUrl + "/oauth-success?token=" + URLEncoder.encode(accessToken, "UTF-8");
//			redirectStrategy.sendRedirect(request, response, redirectUrl);
			
			response.sendRedirect(redirectUrl); // Redirect user to the page after login
		} else {

			HttpSession session = request.getSession(false);
			if (session != null) {
				session.invalidate();
			}

			// Clear authentication
			SecurityContextHolder.clearContext();

			// TODO - redirect to page inform that user is not registered.
			response.sendRedirect("/login-error");
		}
	}
	
	
}
