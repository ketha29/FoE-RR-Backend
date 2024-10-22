package com.ketha.FoE_RoomReservation.service.impl;

import java.sql.Date;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
				
                // Get the start and end dates from the available date list
				Date startDate = availableDateList.get(0);
				Date endDate = availableDateList.get(availableDateList.size() - 1);
				
				// If all the requested booking dates are available then make booking
				if(availableDateList.size() == bookingRequest.getRecurrencePeriod()) {
					Event event = new Event();
					eventRepository.save(event);
					for(Date availableDate : availableDateList) {
						Booking booking = new Booking();
						booking.setStartTime(bookingRequest.getStartTime());
						booking.setEndTime(bookingRequest.getEndTime());
						booking.setRecurrence(bookingRequest.getRecurrence());
						booking.setRecurrencePeriod(bookingRequest.getRecurrencePeriod());
						booking.setDetails(bookingRequest.getDetails());
						booking.setUser(user);
						booking.setRoom(room);
						booking.setDate(availableDate);
						booking.setStartDate(startDate);
						booking.setEndDate(endDate);
						booking.setEvent(event);
						response.setStatusCode(200);
						response.setMessage("Successful");
						bookingRepository.save(booking);
					}
					
//					MailRequestDto request = MailRequestDto.builder()
//							.to(user.getEmail())
//							.userName(user.getUserName())
//							.subject("Booking placed : FOE Room Reservation")
//							.build();
//					
//					Map<String,Object> model = new HashMap<String, Object>();
//					model.put("userName", user.getUserName());
//					model.put("date", availableDateList.toString());
//					model.put("startTime", bookingRequest.getStartTime().toString());
//					model.put("endTime", bookingRequest.getEndTime().toString());
//					model.put("roomName", roomName);		
//					
//					emailService.sendMail(request, model,EmailType.placeBooking);
					
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
	public ResponseDto updateBooking(long bookingId, long userId, Booking bookingRequest) {
		ResponseDto response = new ResponseDto();
		try {
			// Get the selected booking details that is need to be updated
			Booking selectedBooking = bookingRepository.findById(bookingId).orElseThrow(() -> new CustomException("Booking not found"));
			// Get all the bookings associated with the selected event id
			List<Booking> selectedBookingList = bookingRepository.findAllByEventId(selectedBooking.getEvent().getEventId());
			User user = userRepository.findById(userId).orElseThrow(() -> new CustomException("User not found"));
			Room room = roomRepository.findByRoomName(bookingRequest.getRoom().getRoomName()).orElseThrow(() -> new CustomException("Room not found"));
			List<Booking> existingBookings = room.getBookings();
			// Available dates according to the new booking info TODO show the selected booking dates as available
			List<Date> availableDateList = availableDates(bookingRequest, existingBookings);
			
			if(allowToBook(bookingRequest, user, availableDateList)) {
				// If all the dates are available allow to update the booking
				if(availableDateList.size() == bookingRequest.getRecurrencePeriod()) {
					for(Booking booking: selectedBookingList) {
						booking.setStartTime(bookingRequest.getStartTime());
						booking.setEndTime(bookingRequest.getEndTime());
						booking.setRecurrence(bookingRequest.getRecurrence());
						booking.setRecurrencePeriod(bookingRequest.getRecurrencePeriod());
						booking.setDetails(bookingRequest.getDetails());
						booking.setDate(bookingRequest.getDate());
						booking.setUser(user);
						booking.setRoom(room);
						booking.setEvent(selectedBooking.getEvent());
						response.setStatusCode(200);
						response.setMessage("Successful");
						bookingRepository.save(booking);
					}
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
			
//			User user = booking.getUser();
//			
//			MailRequestDto request = MailRequestDto.builder()
//					.to(user.getEmail())
//					.userName(user.getUserName())
//					.subject("Booking cancelled : FOE Room Reservation")
//					.build();
//			
//			Map<String,Object> model = new HashMap<String, Object>();
//			model.put("userName", user.getUserName());
//			model.put("date", booking.getDate().toString());
//			model.put("startTime", booking.getStartTime().toString());
//			model.put("endTime", booking.getEndTime().toString());
//			model.put("roomName", booking.getRoom());		
//			
//			emailService.sendMail(request, model, EmailType.cancelBooking);
			
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
	        // Filter for bookings on the same day
	        .filter(existingBooking -> existingBooking.getDate().toLocalDate().isEqual(currentDate.toLocalDate()))
	        // Check if the requested time does not overlap with any existing booking
	        .noneMatch(existingBooking -> 
	            // The booking should either end exactly when an existing booking starts or start exactly when an existing booking ends
	            !(bookingRequest.getEndTime().toLocalTime().equals(existingBooking.getStartTime().toLocalTime()) || 
	              bookingRequest.getStartTime().toLocalTime().equals(existingBooking.getEndTime().toLocalTime())) && // Allow adjacent times
	            // Check if there is any overlap
	            (bookingRequest.getEndTime().toLocalTime().isAfter(existingBooking.getStartTime().toLocalTime()) && 
	             bookingRequest.getStartTime().toLocalTime().isBefore(existingBooking.getEndTime().toLocalTime()))
	        );
	}
	
	// Check if the user is allowed to book with his requirements 
	private boolean allowToBook(Booking bookingRequest, User user, List<Date> availableDateList) {
		boolean allow = false;
        boolean available = true;
        Calendar calendar = Calendar.getInstance();

        if(user.getUserType() == UserType.regularUser) {
            // Check the booking dates for weekdays and booking time between 8 AM to 5 PM
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

            if(available && (bookingRequest.getRecurrence() == RecurrenceType.none)) {
                allow = true;
            }
		} else if(user.getUserType() == UserType.admin) {
			if((bookingRequest.getRecurrence() == RecurrenceType.none) || 
				((bookingRequest.getRecurrence() == RecurrenceType.daily) && (bookingRequest.getRecurrencePeriod() <= 10)) ||
				((bookingRequest.getRecurrence() == RecurrenceType.weekly) && (bookingRequest.getRecurrencePeriod() <= 5))) {
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
	
	@Override
	public ResponseDto getWeekBooking(Date weekStart, Date weekEnd) {
		ResponseDto response = new ResponseDto();
		
		try {
			List<Booking> bookingList = bookingRepository.getAllWeekBookings(weekStart, weekEnd);
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
	
//	@Override
//	public ResponseDto id(Date date, String roomName) {
//	    ResponseDto response = new ResponseDto();
//	    try {
//	        // Fetch the list of bookings for the given date
//	        List<Booking> bookingList = bookingRepository.findBookingByDate(date);
//	        // Filter the booking list based on the room name
//	        List<Booking> roomBookings = bookingList.stream()
//	            .filter(booking -> booking.getRoom().getRoomName().equalsIgnoreCase(roomName))
//	            .collect(Collectors.toList());
//	        // Sort the bookings by start time
//	        roomBookings.sort(Comparator.comparing(Booking::getStartTime));
//	        
//	        LocalTime openingTime = LocalTime.of(8, 0);
//	        LocalTime closingTime = LocalTime.of(18, 0);
//	        // Initialize the start of free time as the opening time
//	        LocalTime lastEndTime = openingTime;
//	        boolean isFreeTimeAvailable = false;
//	        
//	        // Check gaps between bookings
//	        for (Booking booking : roomBookings) {
//	            LocalTime bookingStartTime = booking.getStartTime().toLocalTime();
//	            LocalTime bookingEndTime = booking.getEndTime().toLocalTime();
//	            if (lastEndTime.isBefore(bookingStartTime)) {
//	                isFreeTimeAvailable = true;
//	                break;
//	            }
//	            // Update the last end time to the current booking's end time
//	            lastEndTime = bookingEndTime;
//	        }
//	        
//	        // Check if there's free time after the last booking before closing time
//	        if (lastEndTime.isBefore(closingTime)) {
//	            isFreeTimeAvailable = true;
//	        }
//	        if (isFreeTimeAvailable) {
//	            response.setStatusCode(200);
//	            response.setMessage("Free time is available for booking.");
//	        } else {
//	            response.setStatusCode(404);
//	            response.setMessage("No free time available for booking on this date.");
//	        }
//	    } catch (Exception e) {
//	        response.setStatusCode(500);
//	        response.setMessage("Error in checking free time: " + e.getMessage());
//	    }
//	    return response;
//	}

}
