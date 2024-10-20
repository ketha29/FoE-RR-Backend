package com.ketha.FoE_RoomReservation.service.interfac;

import java.sql.Date;
import java.util.List;

import com.ketha.FoE_RoomReservation.dto.ResponseDto;
import com.ketha.FoE_RoomReservation.model.Booking;

public interface BookingService {

	ResponseDto getAllbookings();
	
	ResponseDto getBookingById(long bookingId);
	
	ResponseDto getBookingByDate(Date date);
	
//	ResponseDto addBooking(long userId, long roomId, Booking bookingRequest);
	
//	List<Date> addBooking(int userId, int roomId, Booking bookingRequest);

	ResponseDto cancelBooking(long bookingId);

	ResponseDto addBooking(long userId, String roomName, Booking bookingRequest);

	ResponseDto updateBooking(long bookingId, long userId, Booking bookingRequest);

	ResponseDto getWeekBooking(Date weekStart, Date weekEnd);
}
