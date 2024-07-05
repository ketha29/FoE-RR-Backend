package com.ketha.FoE_RoomReservation.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ketha.FoE_RoomReservation.model.User;
import com.ketha.FoE_RoomReservation.repository.UserRepository;

@Service
public class UserService implements UserDetailsService{
	
	UserRepository repository;
	
	// Setter for dependency injection
	// UserService has its UserRepository dependency set at the time of creation
	@Autowired
	public void setRepository(UserRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		Optional<User> user = repository.findByUserName(userName);
		if(user.isPresent()) {
			var userObj = user.get();
			return org.springframework.security.core.userdetails.User.builder()
					.username(userObj.getUserName())
					.password(userObj.getPassword())
					.roles(userObj.getUserType().toString())
					.build();
		}
		else {
			throw new UsernameNotFoundException(userName);
		}
	}
	
	// Get all the user object
	public List<User> getUser() {
		return repository.findAll();
	}

	// Get a user object using the userId
	public User getUserById(int userId) {
		return repository.findById(userId).orElse(new User());
	}
	
	// Add a new user object
	public void addUser(User user) {
		repository.save(user);
	}
	
	// Update the details of an existing user object
	public void updateUser(User user) {
		repository.save(user);
	}

	// Find the user object using the userId, and delete that user object
	public void deleteUser(int userId) {
		repository.deleteById(userId);
	}
}
