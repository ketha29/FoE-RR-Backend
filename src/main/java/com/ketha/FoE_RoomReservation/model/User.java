package com.ketha.FoE_RoomReservation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;
	private String email;
	private long phoneNo;
	private String userName;
	private String password;
	@Enumerated(EnumType.STRING)
	private UserType userType;
	
	public enum UserType {regularUser, admin, superAdmin}
	
	public User() {
		
	}
	
	public User(String email, long phoneNo, String userName, String password, UserType userType) {
		this.email = email;
		this.phoneNo = phoneNo;
		this.userName = userName;
		this.password = password;
		this.userType = userType;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public String getEmail() {
		return email;
	}
	
	public long getPhoneNo() {
		return phoneNo;
	}

	public String getUserName() {
		return userName;
	}
	
	public String getPassword() {
		return password;
	}

	public UserType getUserType() {
		return userType;
	}
}
