package com.ketha.FoE_RoomReservation.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ketha.FoE_RoomReservation.model.User;
import com.ketha.FoE_RoomReservation.repository.UserRepository;

@Configuration
public class CustomUserDetailsService implements UserDetailsService{
	
	private UserRepository repository;
	
	@Autowired
	public CustomUserDetailsService(UserRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		User user = repository.findByUserName(userName).orElseThrow(() -> new UsernameNotFoundException("User not found"));
		return org.springframework.security.core.userdetails.User.builder()
				.username(user.getUserName())
				.password(user.getPassword())
                .authorities(new SimpleGrantedAuthority(user.getUserType().toString()))
				.build();
	}
}