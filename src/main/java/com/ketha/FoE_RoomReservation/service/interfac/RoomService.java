package com.ketha.FoE_RoomReservation.service.interfac;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

import com.ketha.FoE_RoomReservation.dto.ResponseDto;
import com.ketha.FoE_RoomReservation.model.Booking.RecurrenceType;
import com.ketha.FoE_RoomReservation.model.Room;

public interface RoomService {

	ResponseDto getAllRooms();
	
	ResponseDto getRoomById(long roomId);
	
//	ResponseDto getAvailableRoomsByDate(Time startTime, Time endTime, Date date);
	
	ResponseDto addRoom(int capacity, String roomName, String description);
	
	ResponseDto deleteRoom(long roomId);
	
	ResponseDto updateRoom(long roomId, Integer capacity, String roomName, String description);

	ResponseDto getAvailableRoomsByDate(Date date);
}