package com.ketha.FoE_RoomReservation.service.impl;

import java.sql.Date;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ketha.FoE_RoomReservation.dto.BookingDto;
import com.ketha.FoE_RoomReservation.dto.ResponseDto;
import com.ketha.FoE_RoomReservation.email.api.dto.MailRequestDto;
import com.ketha.FoE_RoomReservation.email.api.service.EmailService;
import com.ketha.FoE_RoomReservation.email.api.service.EmailService.EmailType;
import com.ketha.FoE_RoomReservation.exception.CustomException;
import com.ketha.FoE_RoomReservation.model.Booking;
import com.ketha.FoE_RoomReservation.model.Booking.RecurrenceType;
import com.ketha.FoE_RoomReservation.model.Event;
import com.ketha.FoE_RoomReservation.model.Room;
import com.ketha.FoE_RoomReservation.model.User;
import com.ketha.FoE_RoomReservation.model.User.UserType;
import com.ketha.FoE_RoomReservation.repository.BookingRepository;
import com.ketha.FoE_RoomReservation.repository.EventRepository;
import com.ketha.FoE_RoomReservation.repository.RoomRepository;
import com.ketha.FoE_RoomReservation.repository.UserRepository;
import com.ketha.FoE_RoomReservation.service.interfac.BookingService;
import com.ketha.FoE_RoomReservation.utils.Utils;

@Service
public class BookingServiceImpl implements BookingService{

	private BookingRepository bookingRepository;
	private UserRepository userRepository;
	private RoomRepository roomRepository;
	private EventRepository eventRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, RoomRepository roomRepository, EventRepository eventRepository) {
		this.bookingRepository = bookingRepository;
		this.userRepository = userRepository;
		this.roomRepository = roomRepository;
		this.eventRepository = eventRepository;
	}
	
	@Override
	public ResponseDto getAllbookings() {
		ResponseDto response = new ResponseDto();
		
		try {
			List<Booking> bookingList = bookingRepository.findAll();
			List<BookingDto> bookingDtoList = Utils.mapBookingListToBookingListDto(bookingList);
			response.setStatusCode(200);
			response.setMessage("Successful");
			response.setBookingList(bookingDtoList);
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error in getting all the rooms: " + e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseDto getBookingById(long bookingId) {
		ResponseDto response = new ResponseDto();
		
		try {
			Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new CustomException("Booking not found"));
			BookingDto bookingDto = Utils.mapBookingToBookingDto(booking);
			response.setStatusCode(200);
			response.setMessage("Successful");
			response.setBooking(bookingDto);
		} catch (CustomException e) {
			response.setStatusCode(404);
			response.setMessage(e.getMessage());
		}
		catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error in getting the booking: " + e.getMessage());
		}
		return response;
	}
	
	@Override
	// TODO verify that the login user id corresponds with the userId
	public ResponseDto addBooking(long userId, String roomName, Booking bookingRequest) {
		ResponseDto response = new ResponseDto();
		
		try {
			User user = userRepository.findById(userId).orElseThrow(() -> new CustomException("User not found"));
			Room room = roomRepository.findByRoomName(roomName).orElseThrow(() -> new CustomException("Room not found"));
//			Room room = roomRepository.findById(roomId).orElseThrow(() -> new CustomException("Room not found"));
			List<Booking> existingBookings = room.getBookings();
			List<Date> availableDateList = availableDates(bookingRequest, existingBookings);
			
			if(allowToBook(bookingRequest, user, availableDateList)) {
				// If the recurrence type is none then set the default value 0 for recurrence period
				if((bookingRequest.getRecurrence() == RecurrenceType.none)) {
					bookingRequest.setRecurrencePeriod(1);
				}
				
				// If all the requested booking dates are available then make booking
				if(availableDateList.size() == bookingRequest.getRecurrencePeriod()) {
					Event event = new Event();
					eventRepository.save(event);
					for(Date availableDate : availableDateList) {
						Booking booking = new Booking();
						booking.setStartTime(bookingRequest.getStartTime());
						booking.setEndTime(bookingRequest.getEndTime());
						booking.setRecurrence(bookingRequest.getRecurrence());
						booking.setDetails(bookingRequest.getDetails());
						booking.setUser(user);
						booking.setRoom(room);
						booking.setDate(availableDate);
						booking.setEvent(event);
						response.setStatusCode(200);
						response.setMessage("Successful");
						bookingRepository.save(booking);
					}
					
					MailRequestDto request = MailRequestDto.builder()
							.to(user.getEmail())
							.userName(user.getUserName())
							.subject("Booking placed : FOE Room Reservation")
							.build();
					
					Map<String,Object> model = new HashMap<String, Object>();
					model.put("userName", user.getUserName());
					model.put("date", availableDateList.toString());
					model.put("startTime", bookingRequest.getStartTime().toString());
					model.put("endTime", bookingRequest.getEndTime().toString());
					model.put("roomName", roomName);		
					
					emailService.sendMail(request, model,EmailType.placeBooking);
					
				} else {
					throw new CustomException("Rooms are not available for those selected dates");
		        }
			} else {
				response.setStatusCode(403);
	            response.setMessage("Forbidden: not allowed to book with these specification");
			}
		} catch(CustomException e) {
			response.setStatusCode(404);
			response.setMessage(e.getMessage());
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error in adding the booking: " + e.getMessage());
		}
		return response;
	}
	
	@Override
	public ResponseDto cancelBooking(long bookingId) {
		ResponseDto response = new ResponseDto();
		
		try {
			Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new CustomException("Booking not found"));
			long eventId = booking.getEvent().getEventId();
			List<Booking> recurringBookings  = bookingRepository.findAllByEventId(eventId);
			List<Long> bookingIds = recurringBookings.stream().map(recurringBooking -> recurringBooking.getBookingId()).toList();
			bookingRepository.deleteAllById(bookingIds);
			eventRepository.deleteById(eventId);
			List<BookingDto> bookingDto = Utils.mapBookingListToBookingListDto(recurringBookings);
			response.setStatusCode(200);
			response.setMessage("Successful");
			response.setBookingList(bookingDto);
			
			User user = booking.getUser();
			
			MailRequestDto request = MailRequestDto.builder()
					.to(user.getEmail())
					.userName(user.getUserName())
					.subject("Booking cancelled : FOE Room Reservation")
					.build();
			
			Map<String,Object> model = new HashMap<String, Object>();
			model.put("userName", user.getUserName());
			model.put("date", booking.getDate().toString());
			model.put("startTime", booking.getStartTime().toString());
			model.put("endTime", booking.getEndTime().toString());
			model.put("roomName", booking.getRoom());		
			
			emailService.sendMail(request, model, EmailType.cancelBooking);
			
		} catch (CustomException e) {
			response.setStatusCode(404);
			response.setMessage(e.getMessage());
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error in deleting the booking: " + e.getMessage());
		}
		return response;
	}
	
	// Get an array of available booking dates for the specific room
	private List<Date> availableDates(Booking bookingRequest, List<Booking> existingBookings) {
		
		Date date = bookingRequest.getDate();
		RecurrenceType recurrence = bookingRequest.getRecurrence();
		int recurrencePeriod = bookingRequest.getRecurrencePeriod();
		List<Date> availableDateList = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		switch(recurrence) {
			case RecurrenceType.none:
				Date currentDate1 =  new Date(calendar.getTimeInMillis());
				if(roomIsAvailable(currentDate1, bookingRequest, existingBookings)) {
					availableDateList.add(currentDate1);
				}
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				break;
			case RecurrenceType.daily:
				for(int i = 0; i < recurrencePeriod; i++) {
					Date currentDate2 =  new Date(calendar.getTimeInMillis());
					if(roomIsAvailable(currentDate2, bookingRequest, existingBookings)) {
						availableDateList.add(currentDate2);
					}
					calendar.add(Calendar.DAY_OF_MONTH, 1);
				}
				break;
			case RecurrenceType.weekly:
				for(int i = 0; i < recurrencePeriod; i++) {
					Date currentDate3 =  new Date(calendar.getTimeInMillis());
					if(roomIsAvailable(currentDate3, bookingRequest, existingBookings)) {
						availableDateList.add(currentDate3);
					}
					calendar.add(Calendar.WEEK_OF_MONTH, 1);
				}
				break;
		}
		return availableDateList;
	}
	
	/*
	 * Check if the room is available for booking
	 * Room is available if 
	 * booking date should be different and 
	 * the start time of the the booking is after the end time of all existing booking or
	 * the end time of the booking is before the end time of the all existing bookings
	 */
	private boolean roomIsAvailable(Date currentDate, Booking bookingRequest, List<Booking> existingBookings) {
		return existingBookings.stream()
				.noneMatch(existingBooking -> 
					existingBooking.getDate().toLocalDate().isEqual(currentDate.toLocalDate()) &&(
						!(bookingRequest.getEndTime().toLocalTime().isBefore(existingBooking.getStartTime().toLocalTime()) || 
								bookingRequest.getStartTime().toLocalTime().isAfter(existingBooking.getEndTime().toLocalTime())))
				);
	}
	
	// Check if the user is allowed to book with his requirements 
	private boolean allowToBook(Booking bookingRequest, User user, List<Date> availableDateList) {
		boolean allow = false;
        boolean available = true;
        Calendar calendar = Calendar.getInstance();

        if(user.getUserType() == UserType.regularUser) {
            // Check the booking dates for weekdays and booking time between 8 AM to 5 PM
        	// TODO use external calendar api to get the academic days
            for(Date date : availableDateList) {
                calendar.setTime(date);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                if(dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                    available = false;
                    break;
                }  
                if(bookingRequest.getStartTime().toLocalTime().isBefore(LocalTime.parse("08:00:00")) || 
                		bookingRequest.getEndTime().toLocalTime().isAfter(LocalTime.parse("17:00:00"))) {
                    available = false;
                    break;
                }
            }

            if(available && (bookingRequest.getRecurrence() == RecurrenceType.none) || 
                ((bookingRequest.getRecurrence() == RecurrenceType.daily) && (bookingRequest.getRecurrencePeriod() <= 3)) ||
                ((bookingRequest.getRecurrence() == RecurrenceType.weekly) && (bookingRequest.getRecurrencePeriod() <= 4))) {
                allow = true;
            }
		} else if(user.getUserType() == UserType.admin) {
			if((bookingRequest.getRecurrence() == RecurrenceType.none) || 
				((bookingRequest.getRecurrence() == RecurrenceType.daily) && (bookingRequest.getRecurrencePeriod() <= 7)) ||
				((bookingRequest.getRecurrence() == RecurrenceType.weekly) && (bookingRequest.getRecurrencePeriod() <= 16))) {
					allow = true;
			}
		}
		return allow;
	}
	
	@Override
	public ResponseDto getBookingByDate(Date date) {
		ResponseDto response = new ResponseDto();
		
		try {
			List<Booking> bookingList = bookingRepository.findBookingByDate(date);
			List<BookingDto> bookingDtoList = Utils.mapBookingListToBookingListDto(bookingList);
			response.setStatusCode(200);
			response.setMessage("Successful");
			response.setBookingList(bookingDtoList);
		} catch (CustomException e) {
			response.setStatusCode(404);
			response.setMessage(e.getMessage());
		}
		catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error in getting the bookings: " + e.getMessage());
		}
		return response;
	}
}
