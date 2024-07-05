package com.ketha.FoE_RoomReservation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ketha.FoE_RoomReservation.model.User;
import com.ketha.FoE_RoomReservation.repository.UserRepository;

@Service
public class UserService {
	
	UserRepository repositry;
	
	// Setter for dependency injection
	// UserService has its UserRepository dependency set at the time of creation
	@Autowired
	public void setRepository(UserRepository repository) {
		this.repositry = repository;
	}
	
	// Get all the user object
	public List<User> getUser() {
		return repositry.findAll();
	}

	// Get a user object using the userId
	public User getUserById(int userId) {
		return repositry.findById(userId).orElse(new User());
	}
	
	// Add a new user object
	public void addUser(User user) {
		repositry.save(user);
	}
	
	// Update the details of an existing user object
	public void updateUser(User user) {
		repositry.save(user);
	}

	// Find the user object using the userId, and delete that user object
	public void deleteUser(int userId) {
		repositry.deleteById(userId);
	}
}
