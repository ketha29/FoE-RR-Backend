package com.ketha.FoE_RoomReservation.service.impl;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ketha.FoE_RoomReservation.dto.ResponseDto;
import com.ketha.FoE_RoomReservation.dto.RoomDto;
import com.ketha.FoE_RoomReservation.exception.CustomException;
import com.ketha.FoE_RoomReservation.model.Booking.RecurrenceType;
import com.ketha.FoE_RoomReservation.model.Room;
import com.ketha.FoE_RoomReservation.repository.RoomRepository;
import com.ketha.FoE_RoomReservation.service.interfac.RoomService;
import com.ketha.FoE_RoomReservation.utils.Utils;

@Service
public class RoomServiceImpl implements RoomService{
	
	private RoomRepository roomRepository;

	public RoomServiceImpl(RoomRepository roomRepository) {
		this.roomRepository = roomRepository; 
	}
	
	@Override
	public ResponseDto getAllRooms() {
		ResponseDto response = new ResponseDto();
		
		try {
			List<Room> roomList = roomRepository.findAll();
			if (roomList.isEmpty()) {
		        System.out.println("No rooms found in the database.");
		    } else {
		        System.out.println("Rooms found: " + roomList.size());
		    }
			List<RoomDto> roomDtoList = Utils.mapRoomListToRoomListDto(roomList);
			response.setStatusCode(200);
			response.setMessage("Successful");
			response.setRoomList(roomDtoList);
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error in adding the new room: " + e.getMessage());
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
	public ResponseDto getAvailableRoomsByDate(Time startTime, Time endTime, Date date) {
		ResponseDto response = new ResponseDto();
		// TODO not working properly
		try {
			List<Room> roomList = roomRepository.findAvailableRoomsByDate(startTime, endTime, date);
			List<RoomDto> roomDtoList = Utils.mapRoomListToRoomListDto(roomList);
			response.setStatusCode(200);
			response.setMessage("Successful");
			response.setRoomList(roomDtoList);
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error in getting the room: " + e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseDto addRoom(int capacity, String roomName, String description) {
		ResponseDto response = new ResponseDto();

		try {
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
