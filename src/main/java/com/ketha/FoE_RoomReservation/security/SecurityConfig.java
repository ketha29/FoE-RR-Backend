package com.ketha.FoE_RoomReservation.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	

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
	
	
	// Create in memory users
	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails regularUser = User.builder()
				.username("regularUser")
				.password("$2a$12$mJ7XBexiUv8DYAO9dxZwO.Mfj1jtvdcOPxC2uDHMXsVQbvX0H.Woa")	// 123
				.roles("regularUser")
				.build();
		UserDetails admin = User.builder()
				.username("admin")
				.password("$2a$12$DW2EFWgP294/29mMywSEVeI9vlTyAAZMpfppGiXcbCBwUYWCy/5b2")	// 456
				.roles("admin")
				.build();
		UserDetails superAdmin = User.builder()
				.username("super")
				.password("$2a$12$O5wOTViRddt2B03HyTSFl.LYJeh29FHjUc9RZvm2CZ804GxQ81WtG")	// 789
				.roles("superAdmin")
				.build();
		return new InMemoryUserDetailsManager(regularUser, admin, superAdmin);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
