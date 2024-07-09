package com.ketha.FoE_RoomReservation.dto;

import java.util.List;

import com.ketha.FoE_RoomReservation.model.User.UserType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDto {

	private int StatusCode;
	private String message;
	private UserType userType;
	private UserDto userDto;
	private List<UserDto> userList;
}
