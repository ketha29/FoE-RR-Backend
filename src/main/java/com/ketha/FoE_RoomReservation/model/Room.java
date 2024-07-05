package com.ketha.FoE_RoomReservation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Room {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int roomId;
	private int capacity;
	private String roomName;
	
	public Room() {
		
	}
	
	public Room(int capacity, String roomName) {
		this.capacity = capacity;
		this.roomName = roomName;
	}
	
	public int getRoomId() {
		return roomId;
	}
	
	public int getCapacity() {
		return capacity;
	}

	public String getRoomName() {
		return roomName;
	}
}
