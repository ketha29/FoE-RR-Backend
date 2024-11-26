package com.ketha.FoE_RoomReservation.repository;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ketha.FoE_RoomReservation.model.Room;
 
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
	
	Optional<Room> findByRoomName(String room);
	
	boolean existsByRoomName(String roomName);

    @Query("SELECT r FROM Room r WHERE r.roomId NOT IN (SELECT b.room.roomId FROM Booking b WHERE (b.startTime < :startTime) AND (b.endTime > :endTime) AND (b.date = :date))")
	List<Room> findAvailableRoomsByDate(Time startTime,  Time endTime, Date date);
}
