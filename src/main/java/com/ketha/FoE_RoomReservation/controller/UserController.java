package com.ketha.FoE_RoomReservation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ketha.FoE_RoomReservation.model.User;
import com.ketha.FoE_RoomReservation.service.UserService;

@RestController
public class UserController {

	UserService service;
	
	// Setter for dependency injection
	// UserController has its UserService dependency set at the time of creation
	@Autowired
	public void setService(UserService service) {
		this.service = service;
	}
	
	@GetMapping("admin/getUser")
	public List<User> getUser() {
		return service.getUser();
	}
	
	@GetMapping("/getUser/{userId}")
	public User getUserById(@PathVariable int userId) {
		return service.getUserById(userId);
	}
	
	@PostMapping("/registerUser")
	public void addUser(@RequestBody User userDetails) {
		service.addUser(userDetails);
	}
	
	@PutMapping("/updateUser")
	public void updateUser(@RequestBody User userDetails) {
		service.updateUser(userDetails);
	}
	
	@DeleteMapping("/deleteUser/{userId}")
	public void deleteUser(@PathVariable int userId) {
		service.deleteUser(userId);
	}
}
