package com.ketha.FoE_RoomReservation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ketha.FoE_RoomReservation.model.Room;
import com.ketha.FoE_RoomReservation.repository.RoomRepository;

@Service
public class RoomService {
	
private RoomRepository repo;
	
	@Autowired
	public void setRepo(RoomRepository repo) {
		this.repo = repo;
	}
	
	// Get all the room objects
	public List<Room> getAllRooms() {
		return repo.findAll();
	}
	
	// Get a room object using the id, if not found return a null object
	public Room getRoomById(int roomId) {
		return repo.findById(roomId).orElse(new Room());
	}
	
	// Add a new room object to the room table
	public void addRoom(Room roomDetails) {
		repo.save(roomDetails);
	}
	
	// Update the details about a room object
	public void updateRoom(Room roomDetails) {
		repo.save(roomDetails);
	}
	
	// Delete a room object using the room id
	public void deleteRoomById(int roomId) {
		repo.deleteById(roomId);
	}
}
