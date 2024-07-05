package com.ketha.FoE_RoomReservation.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Room {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int roomId;
	private int capacity;
	private String roomName;
	
	@OneToMany(mappedBy = "roomId", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Booking> bookings = new ArrayList<Booking>();
	
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
