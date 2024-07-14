package com.ketha.FoE_RoomReservation.dto;

import com.ketha.FoE_RoomReservation.model.User.UserType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterDto {

	private long userId;
	private String email;
	private long phoneNo;
	private String userName;
	private String password;
	private UserType userType;
}
