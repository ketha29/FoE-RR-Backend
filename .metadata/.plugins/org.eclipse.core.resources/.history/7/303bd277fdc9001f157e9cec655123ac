package com.ketha.FoE_RoomReservation.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Configuration
public class JwtAuthFilter extends OncePerRequestFilter{
	
	private final JwtService jwtService;
	
	 @Autowired
    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

	@Override
	protected void doFilterInternal(
		@NonNull HttpServletRequest request, 
		@NonNull HttpServletResponse response, 
		@NonNull FilterChain filterChain
	) throws ServletException, IOException {
		final String authHeader = request.getHeader("Authorization");		
		final String jwt;
		final String username;
				
		// Check jwt token
		if(authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.startsWith("Bearer null") ) {
			filterChain.doFilter(request, response);
			return;
		}
		
		// Extract token from token header
		jwt = authHeader.substring(7);
		username = jwtService.extractUsername(jwt);
		
		System.err.println(username);
//		// Check if the user name is not null and user is not jet authenticated
//		if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//			UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
//			/*
//			 * Check if the token is till valid or not
//			 * Valid: update the SecurityContext and send the request to DispatcherServlet
//			 */
//			if(jwtService.isTokenValid(jwt, userDetails)) {
//				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//					userDetails,
//					null,
//					userDetails.getAuthorities()
//				); 
//				authToken.setDetails(
//					new WebAuthenticationDetailsSource().buildDetails(request)
//				);
//				SecurityContextHolder.getContext().setAuthentication(authToken);
//			} 
//		}
		filterChain.doFilter(request, response);
	}
}