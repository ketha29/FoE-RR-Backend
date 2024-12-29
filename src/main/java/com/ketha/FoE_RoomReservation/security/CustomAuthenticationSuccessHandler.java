package com.ketha.FoE_RoomReservation.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	
	private UserRepository userRepository;
	
	@Autowired
	CustomAuthenticationSuccessHandler(UserRepository userRepository){
		this.userRepository = userRepository;
	}
	
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        
    	OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    	String email = oAuth2User.getAttribute("email");
    	if(userRepository.existsByEmail(email)) {
    		// Load the user
    		User user = userRepository.findByEmail(email).orElseThrow(()->new CustomException("User not found"));
    		
    		// Grant authority
    		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
    		oAuth2User.getAuthorities().forEach(ga->authorities.add(ga));
    		user.getAuthorities().forEach(ga->authorities.add(ga));
    		    		        	
            OAuth2AuthenticationToken updatedAuthentication = new OAuth2AuthenticationToken(oAuth2User, authorities, "sub");
        	SecurityContextHolder.getContext().setAuthentication(updatedAuthentication);

        	response.sendRedirect("http://localhost:5173/booking/month"); // Redirect user to the page after login
    	} else {
    		
    		HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            // Clear authentication
            SecurityContextHolder.clearContext();
            
    		// TODO - redirect to page inform that user is not registered.
			response.sendRedirect("/auth/current-user");
		}
    }
}
