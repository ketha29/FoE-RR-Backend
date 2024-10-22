package com.ketha.FoE_RoomReservation.service.impl;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ketha.FoE_RoomReservation.dto.ResponseDto;
import com.ketha.FoE_RoomReservation.dto.RoomDto;
import com.ketha.FoE_RoomReservation.exception.CustomException;
import com.ketha.FoE_RoomReservation.model.Booking;
import com.ketha.FoE_RoomReservation.model.Booking.RecurrenceType;
import com.ketha.FoE_RoomReservation.model.Room;
import com.ketha.FoE_RoomReservation.repository.BookingRepository;
import com.ketha.FoE_RoomReservation.repository.RoomRepository;
import com.ketha.FoE_RoomReservation.service.interfac.RoomService;
import com.ketha.FoE_RoomReservation.utils.Utils;

@Service
public class RoomServiceImpl implements RoomService{
	
	private RoomRepository roomRepository;
	private BookingRepository bookingRepository;

	public RoomServiceImpl(RoomRepository roomRepository, BookingRepository bookingRepository) {
		this.roomRepository = roomRepository; 
		this.bookingRepository = bookingRepository; 
	}
	
	@Override
	public ResponseDto getAllRooms() {
		ResponseDto response = new ResponseDto();
		
		try {
			List<Room> roomList = roomRepository.findAll();
//			if (roomList.isEmpty()) {
//		        System.out.println("No rooms found in the database.");
//		    } else {
//		        System.out.println("Rooms found: " + roomList.size());
//		    }
			List<RoomDto> roomDtoList = Utils.mapRoomListToRoomListDto(roomList);
			response.setStatusCode(200);
			response.setMessage("Successful");
			response.setRoomList(roomDtoList);
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error getting rooms: " + e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseDto getRoomById(long roomId) {
		ResponseDto response = new ResponseDto();
		
		try {
			Room room = roomRepository.findById(roomId).orElseThrow(() -> new CustomException("Room not found"));
			RoomDto roomDto = Utils.mapRoomToRoomDto(room);
			response.setStatusCode(200);
			response.setMessage("Successful");
			response.setRoom(roomDto);
		} catch (CustomException e) {
			response.setStatusCode(404);
			response.setMessage(e.getMessage());
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error in getting all the rooms: " + e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseDto getAvailableRoomsByDate(Date date) {
	    ResponseDto response = new ResponseDto();
	    try {
	        // Define booking opening and closing times for the day
	        LocalTime bookingOpenTime = LocalTime.of(8, 0);
	        LocalTime bookingEndTime = LocalTime.of(18, 0);
	        
	        // Get all rooms
	        List<Room> allRooms = roomRepository.findAll();

	        // Iterate through each room and check availability within the booking window
	        List<Room> availableRooms = new ArrayList<>();
	        for (Room room : allRooms) {
				// Get all bookings for this room on the given date
	            List<Booking> roomBookings = bookingRepository.findByDateAndRoom_RoomName(date, room.getRoomName());
	            
	            // Sort bookings by start time
	            roomBookings.sort(Comparator.comparing(Booking::getStartTime));

	            // Initialize time tracker for availability
	            LocalTime lastEndTime = bookingOpenTime;
	            boolean isRoomAvailable = false;
	            
	            // Check gaps between bookings
	            for (Booking booking : roomBookings) {
	                LocalTime bookingStartTime = booking.getStartTime().toLocalTime();	           
	                // Check if there's free time before the current booking starts
	                if (lastEndTime.isBefore(bookingStartTime)) {
	                    isRoomAvailable = true;
	                    break;
	                }	                
	                lastEndTime = booking.getEndTime().toLocalTime();
	            }
	            if (lastEndTime.isBefore(bookingEndTime)) {
	                isRoomAvailable = true;
	            }
	            if (isRoomAvailable) {
	                availableRooms.add(room);
	            }
	        }
	        List<RoomDto> availableRoomDtoList = Utils.mapRoomListToRoomListDto(availableRooms);
	        response.setStatusCode(200);
	        response.setMessage("Successful");
	        response.setRoomList(availableRoomDtoList);

	    } catch (Exception e) {
	        response.setStatusCode(500);
	        response.setMessage("Error in getting available rooms: " + e.getMessage());
	    }
	    return response;
	}


	@Override
	public ResponseDto addRoom(int capacity, String roomName, String description) {
		ResponseDto response = new ResponseDto();

		try {
	        // Check if a room with the same roomName already exists
			if(roomRepository.existsByRoomName(roomName)) {
				response.setStatusCode(400);
				response.setMessage("Room with name '" + roomName + "' already exists");
				return response;
			}
			if(capacity <= 0) {
				response.setStatusCode(400);
				response.setMessage("Room capacity should be positive");
				return response;
			}
			Room room = new Room();
			room.setCapacity(capacity);
			room.setRoomName(roomName);
			room.setDescription(description);
			Room savedRoom = roomRepository.save(room);
			RoomDto roomDto = Utils.mapRoomToRoomDto(savedRoom);
			response.setStatusCode(200);
			response.setMessage("Successful");
			response.setRoom(roomDto);
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error in adding the new room: " + e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseDto deleteRoom(long roomId) {
		ResponseDto response = new ResponseDto();
		
		try {
			Room room = roomRepository.findById(roomId).orElseThrow(() -> new CustomException("Room not found"));
			RoomDto roomDto = Utils.mapRoomToRoomDto(room);
			roomRepository.delete(room);
			response.setStatusCode(200);
			response.setMessage("Successful");
			response.setRoom(roomDto);
		} catch (CustomException e) {
			response.setStatusCode(404);
			response.setMessage(e.getMessage());
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error in deleting the room: " + e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseDto updateRoom(long roomId, Integer capacity, String roomName, String description) {
		ResponseDto response = new ResponseDto();

		try {
			Room room = roomRepository.findById(roomId).orElseThrow(() -> new CustomException("Room not found"));
			if(capacity <= 0) {
				response.setStatusCode(400);
				response.setMessage("Room capacity should be positive");
				return response;
			}
			if(capacity != null) room.setCapacity(capacity);
			if(roomName != null) room.setRoomName(roomName);
			if(description != null) room.setDescription(description);
			
			Room updatedRoom = roomRepository.save(room);
			RoomDto roomDto = Utils.mapRoomToRoomDto(updatedRoom);
			
			response.setStatusCode(200);
			response.setMessage("Successful");
			response.setRoom(roomDto);
			
		} catch (CustomException e) {
			response.setStatusCode(404);
			response.setMessage(e.getMessage());
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error in adding the new room: " + e.getMessage());
		}
		return response;
	}
}
