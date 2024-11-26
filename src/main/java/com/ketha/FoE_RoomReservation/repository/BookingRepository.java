package com.ketha.FoE_RoomReservation.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ketha.FoE_RoomReservation.model.Booking;
import com.ketha.FoE_RoomReservation.model.Event;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
	
	@Query("SELECT b FROM Booking b WHERE b.event.eventId = :eventId")
	List<Booking> findAllByEventId(@Param("eventId") long eventId);
	
	@Query("SELECT b FROM Booking b WHERE b.date = :date")
	List<Booking> findBookingByDate(@Param("date") Date date);
	
	@Query("SELECT b FROM Booking b WHERE b.date >= :startDate AND b.date <= :endDate")
	List<Booking> getAllWeekBookings(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
	
	@Query("SELECT b FROM Booking b WHERE b.date = :date AND b.room.roomName = :roomName")
    List<Booking> findByDateAndRoom_RoomName(@Param("date") Date date, @Param("roomName") String roomName);}