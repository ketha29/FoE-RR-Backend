package com.ketha.FoE_RoomReservation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ketha.FoE_RoomReservation.model.Room;
import com.ketha.FoE_RoomReservation.service.RoomService;


@RestController
public class RoomController {

	RoomService service;
	
	@Autowired
	public void setService(RoomService service) {
		this.service = service;
	}
	
	@RequestMapping("/room")
	public List<Room> getAllRooms() {
		return service.getAllRooms();
	}
	
	@RequestMapping("/room/{roomId}")
	public Room getRoomById(@PathVariable int roomId) {
		return service.getRoomById(roomId);
	}
	
	@PostMapping("/room")
	public void addRoom(@RequestBody Room room) {
		service.addRoom(room);
	}
	
	@PutMapping("/room")
	public void updateRoom(@RequestBody Room room) {
		service.updateRoom(room);
	}
	
	@DeleteMapping("/room/{roomId}")
	public void deleteRoom(@PathVariable int roomId) {
		service.deleteRoomById(roomId);
	}
}
