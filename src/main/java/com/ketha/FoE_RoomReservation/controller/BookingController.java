package com.ketha.FoE_RoomReservation.controller;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ketha.FoE_RoomReservation.dto.ResponseDto;
import com.ketha.FoE_RoomReservation.model.Booking;
import com.ketha.FoE_RoomReservation.service.impl.BookingServiceImpl;

@CrossOrigin("*")
@RestController
@RequestMapping("/booking")
public class BookingController {
	
	private BookingServiceImpl bookingService;
	
	@Autowired
	public BookingController(BookingServiceImpl bookingService) {
		this.bookingService = bookingService;
	}
	
	@GetMapping("/all")
	public ResponseEntity<ResponseDto> getAllBookings() {
		ResponseDto response =  bookingService.getAllbookings();
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}
	
	@GetMapping("/day/{date}")
//	@PreAuthorize("hasAuthority('admin') or hasAuthority('superAdmin')")
	public ResponseEntity<ResponseDto> getBookingByDate(
			@PathVariable("date") Date date
	) {
		if(date == null) {
			ResponseDto response = new ResponseDto();
			response.setStatusCode(400);
			response.setMessage("Date field cannot be empty");
			return ResponseEntity.status(response.getStatusCode()).body(response);
		}
	    java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		ResponseDto response =  bookingService.getBookingByDate(sqlDate);
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}
	
	@GetMapping("/week/{weekStart}/{weekEnd}")
//	@PreAuthorize("hasAuthority('admin') or hasAuthority('superAdmin')")
	public ResponseEntity<ResponseDto> getBookingByDate(
			@PathVariable("weekStart") Date weekStart,
			@PathVariable("weekEnd") Date weekEnd
	) {
		if(weekStart == null || weekEnd == null) {
			ResponseDto response = new ResponseDto();
			response.setStatusCode(400);
			response.setMessage("Date field cannot be empty");
			return ResponseEntity.status(response.getStatusCode()).body(response);
		}
	    java.sql.Date sqlWeekStart = new java.sql.Date(weekStart.getTime());
	    java.sql.Date sqlWeekEnd = new java.sql.Date(weekEnd.getTime());
		ResponseDto response =  bookingService.getWeekBooking(sqlWeekStart, sqlWeekEnd);
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}
	
	@PostMapping("/add-booking/{roomId}/{userId}")
//	@PreAuthorize("hasAuthority('admin') or hasAuthority('superAdmin')")
	public ResponseEntity<ResponseDto> addBooking(
			@PathVariable int userId,
			@PathVariable String roomId,
			@RequestBody Booking bookingRequest
	){
		ResponseDto response = bookingService.addBooking(userId, roomId, bookingRequest);
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}
	
	@PutMapping("/update-booking/{bookingId}/{userId}")
	public ResponseEntity<ResponseDto> updateBooking(
			@PathVariable int bookingId,
			@PathVariable int userId,
			@RequestBody Booking bookingRequest
	) {
		ResponseDto response = bookingService.updateBooking(bookingId, userId, bookingRequest);
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}
	
	@DeleteMapping("/delete-booking/{bookingId}/{userId}")
	public ResponseEntity<ResponseDto> cancelBooking(
			@PathVariable long bookingId,
			@PathVariable long userId
	) {
		ResponseDto response = bookingService.cancelBooking(bookingId, userId);
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}
	
//	@GetMapping("/is-room-available/{date}/{roomName}")
//	public ResponseEntity<ResponseDto> isRoomAvailable(
//			@PathVariable("date") Date date,
//			@PathVariable("roomName") String roomName
//	) {
//		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
//		ResponseDto response =  bookingService.isRoomAvailable(sqlDate, roomName);
//		return ResponseEntity.status(response.getStatusCode()).body(response);
//	}
}
