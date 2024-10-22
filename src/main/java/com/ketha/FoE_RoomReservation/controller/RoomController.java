package com.ketha.FoE_RoomReservation.controller;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ketha.FoE_RoomReservation.dto.ResponseDto;
import com.ketha.FoE_RoomReservation.service.impl.RoomServiceImpl;

@CrossOrigin("*")
@RestController
@RequestMapping("/room")
public class RoomController {

	private RoomServiceImpl roomService;
	
	@Autowired
	public RoomController(RoomServiceImpl roomService) {
		this.roomService = roomService;
	}
	
//	@PreAuthorize("hasAuthority('regularUser'), hasAuthority('admin') or hasAuthority('superAdmin')")
	@GetMapping("/all")
	public ResponseEntity<ResponseDto> getAllRooms() {
		ResponseDto response =  roomService.getAllRooms();
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}
	
	@GetMapping("/room-by-id/{roomId}")
	public ResponseEntity<ResponseDto> getRoomById(@PathVariable int roomId) {
		ResponseDto response =  roomService.getRoomById(roomId);
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}
	
	@GetMapping("/available-rooms-by-date/{date}")
	public ResponseEntity<ResponseDto> getAvailableRoomsByDate(@PathVariable Date date) {
	    java.sql.Date sqlDate = new java.sql.Date(date.getTime());
	    System.out.println("Received date: " + sqlDate);
	    
	    ResponseDto response = roomService.getAvailableRoomsByDate(sqlDate);
	    return ResponseEntity.status(response.getStatusCode()).body(response);
	}

	
	@PostMapping("/add-room")
	public ResponseEntity<ResponseDto> addRoom(
			@RequestParam(value = "capacity", required = false) Integer capacity,
			@RequestParam(value = "roomName", required = false) String roomName,
			@RequestParam(value = "description", required = false) String description
	) {
		if(roomName == null || roomName.isBlank() || capacity == null) {
			ResponseDto response = new ResponseDto();
			response.setStatusCode(400);
			response.setMessage("These fields(roomName, capacity) shouldn't be empty");
			return ResponseEntity.status(response.getStatusCode()).body(response);
		}
		ResponseDto response =  roomService.addRoom(capacity, roomName, description);
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}
	
	@DeleteMapping("/delete-room/{roomId}")
//	@PreAuthorize("hasAuthority('admin') or hasAuthority('superAdmin')")
	public ResponseEntity<ResponseDto> deleteRoom(@PathVariable int roomId) {
		ResponseDto response =  roomService.deleteRoom(roomId);
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}
	
	@PutMapping("/update-room/{roomId}")
	public ResponseEntity<ResponseDto> updateRoom(
			@PathVariable int roomId,
			@RequestParam(value = "capacity", required = false) Integer capacity,
			@RequestParam(value = "roomName", required = false) String roomName,
			@RequestParam(value = "description", required = false) String description
	) {
		ResponseDto response =  roomService.updateRoom(roomId, capacity, roomName, description);
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}
}
