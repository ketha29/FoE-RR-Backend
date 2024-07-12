package com.ketha.FoE_RoomReservation.model;

import java.sql.Date;
import java.sql.Time;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int bookingId;
	private Time startTime;
	private Time endTime;
	private Date date;
	@Enumerated(EnumType.STRING)
	private RecurrenceType recurrence;
	private int recurrencePeriod;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "roomId")
	private Room room;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId")
	private User user;
	
	public enum RecurrenceType {none, daily, weekly};
}
