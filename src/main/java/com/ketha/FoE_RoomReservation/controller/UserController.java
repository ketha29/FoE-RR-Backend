package com.ketha.FoE_RoomReservation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ketha.FoE_RoomReservation.dto.ResponseDto;
import com.ketha.FoE_RoomReservation.model.User;
import com.ketha.FoE_RoomReservation.service.impl.UserServiceImpl;

@CrossOrigin("*")
@RestController
@RequestMapping("/user")
public class UserController {

	private UserServiceImpl service;
	
	// Setter for dependency injection
	// UserController has its UserService dependency set at the time of creation
	@Autowired
	public UserController(UserServiceImpl service) {
		this.service = service;
	}
	 
	@GetMapping("/all")
//	@PreAuthorize("hasAuthority('admin') or hasAuthority('superAdmin')")
	public ResponseEntity<ResponseDto> getAllUsers() {
		ResponseDto response =  service.getAllUsers();
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}
	
	@GetMapping("/get-by-id/{userId}")
	@PreAuthorize("hasAuthority('admin') or hasAuthority('superAdmin')")
	public ResponseEntity<ResponseDto> getUserById(@PathVariable long userId) {
		ResponseDto response =  service.getUserById(userId);
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}
	
	@GetMapping("/get-user-bookings/{userId}")
	@PreAuthorize("hasAuthority('admin') or hasAuthority('superAdmin')")
	public ResponseEntity<ResponseDto> getUserBookings(@PathVariable long userId) {
		ResponseDto response =  service.getUserBookings(userId);
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}
	
	@GetMapping("/get-by-name/{fullName}")
//	@PreAuthorize("hasAuthority('admin') or hasAuthority('superAdmin')")
	public ResponseEntity<ResponseDto> getUserByFullName(@PathVariable String fullName) {
		ResponseDto response =  service.getUserbyFullName(fullName);
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}
	
	@PostMapping("/register")
//	@PreAuthorize("hasAuthority('admin') or hasAuthority('superAdmin')")
	public ResponseEntity<ResponseDto> register(@RequestBody User user) {
		ResponseDto response =  service.register(user);
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}
	
//	@PutMapping("/updateUser")
//	public void updateUser(@RequestBody User userDetails) {
//		service.updateUser(userDetails);
//	}
	
	@DeleteMapping("delete-user/{userId}")
//	@PreAuthorize("hasAuthority('admin') or hasAuthority('superAdmin')")
	public ResponseEntity<ResponseDto> deleteUser(@PathVariable long userId) {
		ResponseDto response =  service.deleteUser(userId);
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}
}
