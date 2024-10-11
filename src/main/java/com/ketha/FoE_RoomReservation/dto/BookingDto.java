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

	private long bookingId;
	private Time startTime;
	private Time endTime;
	private Date date;
	private Date startDate;
	private Date endDate;
	private RecurrenceType recurrence;
	private int recurrencePeriod;
	private String details;
	private UserDto user;
	private RoomDto room;
}
