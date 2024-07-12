package com.ketha.FoE_RoomReservation.repository;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ketha.FoE_RoomReservation.model.Room;
 
@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    @Query("SELECT r FROM Room r WHERE r.roomId NOT IN (SELECT b.room.roomId FROM Booking b WHERE (b.startTime < :startTime) AND (b.endTime > :endTime) AND (b.date = :date))")
	List<Room> findAvailableRoomsByDate(Time startTime,  Time endTime, Date date);
}
