package com.ketha.FoE_RoomReservation.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
	@Value("${frontend.url}")
	private String allowedCrossOrigin;
	
	private CustomAuthenticationSuccessHandler successHandler;

	@Autowired
	public SecurityConfig(CustomAuthenticationSuccessHandler successHandler) {
		this.successHandler = successHandler;
	}

	// Customizing the default security configuration
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.csrf(AbstractHttpConfigurer::disable).cors(cors -> cors.configurationSource(request -> {
			CorsConfiguration config = new CorsConfiguration();
			config.setAllowedOrigins(List.of(allowedCrossOrigin)); // Allow the frontend origin
			config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allowed HTTP methods
			config.setAllowedHeaders(List.of("Authorization", "Content-Type")); // Allowed headers
			config.setAllowCredentials(true); // Allow credentials (cookies, etc.)
			return config;
		})).authorizeHttpRequests(request -> request.requestMatchers("/user/**", "room/**", "/booking/**", "/auth/**")
				.permitAll().anyRequest().authenticated())
				.oauth2Login(oauth2 -> oauth2
						.successHandler(successHandler))
				.logout((logout) -> logout
						.deleteCookies("JSESSIONID", "OAuth2-Token")
						.clearAuthentication(true)
						.invalidateHttpSession(true))
				.build();
	}
}
