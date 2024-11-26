package com.ketha.FoE_RoomReservation.model;

import java.util.ArrayList;
import java.util.List;

import com.ketha.FoE_RoomReservation.model.User.UserType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "rooms")
public class Room {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long roomId;
	@Column(nullable = false)
	private int capacity;
	@Column(unique = true, nullable = false)
	private String roomName;
	private String description;
	
	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Booking> bookings = new ArrayList<Booking>();
}
