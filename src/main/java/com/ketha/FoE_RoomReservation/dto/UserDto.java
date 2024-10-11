package com.ketha.FoE_RoomReservation.dto;

import java.util.ArrayList;
import java.util.List;

import com.ketha.FoE_RoomReservation.model.User.UserType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

	private long userId;
	private String userName;
	private String firstName;
	private String lastName;
	private String email;
	private long phoneNo;
	private UserType userType;
	@Builder.Default
	private List<BookingDto> bookings = new ArrayList<>();
}