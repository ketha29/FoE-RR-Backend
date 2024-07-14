package com.ketha.FoE_RoomReservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ketha.FoE_RoomReservation.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
	
}

