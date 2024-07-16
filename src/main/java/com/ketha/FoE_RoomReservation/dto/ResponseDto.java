package com.ketha.FoE_RoomReservation.dto;

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
public class ResponseDto {

	private int StatusCode;
	private String message;
	
	private UserType userType;
	
	private UserDto user;
	private RoomDto room;
	private BookingDto booking;
	
	private List<UserDto> userList;
	private List<RoomDto> roomList;
	private List<BookingDto> bookingList;
}
