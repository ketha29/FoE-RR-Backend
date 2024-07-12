package com.ketha.FoE_RoomReservation.dto;

import java.sql.Date;
import java.sql.Time;

import com.ketha.FoE_RoomReservation.model.Booking.RecurrenceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {

	private int bookingId;
	private Time startTime;
	private Time endTime;
	private Date date;
	private RecurrenceType recurrence;
	private int recurrencePeriod;
	private UserDto user;
	private RoomDto room;
}
