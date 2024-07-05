package com.ketha.FoE_RoomReservation.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.ketha.FoE_RoomReservation.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private UserService service;
	
	@Autowired
	public void setService(UserService service) {
		this.service = service;
	}

	// Customizing the default security configuration
	@Bean
	public SecurityFilterChain securityFilterChin(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity
				.authorizeHttpRequests(registry -> {
					registry.requestMatchers("/home").permitAll();
					registry.requestMatchers("/regularUser/**").hasAnyRole("regularUser", "admin", "superAdmin");
					registry.requestMatchers("/admin/**").hasAnyRole("admin", "superAdmin");
					registry.requestMatchers("/superAdmin/**").hasRole("superAdmin");
					registry.anyRequest().authenticated();
				})
				.formLogin(formLogin -> formLogin.permitAll())
				.build();
	}
	
	@Bean
	public UserDetailsService userDetailsService() {
		return service;
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(service);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
