package com.ketha.FoE_RoomReservation.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ketha.FoE_RoomReservation.service.impl.JwtTokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Autowired
	public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {
		
		String path = request.getRequestURI();
		if (path.equals("/auth/refresh")) {
		    filterChain.doFilter(request, response); // don't try to auth
		    return;
		}
		
		try {
			// 1. Extract token from Authorization header
			String token = extractJwtFromRequest(request);

			// 2. Validate token
			if (token != null && jwtTokenProvider.validateToken(token)) {
				// 3. Get user identity from token
				String username = jwtTokenProvider.getUsernameFromToken(token);

				// 4. Create authentication object
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,
						null, jwtTokenProvider.getAuthoritiesFromToken(token));

				// 5. Set the security context
				SecurityContextHolder.getContext().setAuthentication(authentication);

				filterChain.doFilter(request, response);
				return;
			}

//			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			// If user does have a token just pass the filter
			filterChain.doFilter(request, response);

		} catch (ExpiredJwtException ex) {
			// Token expired - let the response interceptor handle refresh
			logger.debug("JWT token expired");

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;

		} catch (Exception ex) {
			logger.error("Failed to set user authentication", ex);

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
	}

	private String extractJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}