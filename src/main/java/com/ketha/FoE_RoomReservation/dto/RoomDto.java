package com.ketha.FoE_RoomReservation.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDto {

	private long roomId;
	private int capacity;
	private String roomName;
	private String description;
	private List<BookingDto> bookings;
}
