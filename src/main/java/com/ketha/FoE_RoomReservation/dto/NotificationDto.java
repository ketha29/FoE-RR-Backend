package com.ketha.FoE_RoomReservation.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDto {
		
	private UserDto loggedUser;
	private UserDto bookedByUser;
	private UserDto bookForUser;
	private String purpose;
	private List<String> bookingDates;
	private String startTime;
	private String endTime;
	private String roomName;
	
}
