package com.ketha.FoE_RoomReservation.dto;

import java.util.ArrayList;
import java.util.List;

import com.ketha.FoE_RoomReservation.model.User.UserType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

	private long userId;
	private String email;
	private long phoneNo;
	private String userName;
	private UserType userType;
	private List<BookingDto> bookings = new ArrayList<>();
}