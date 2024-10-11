package com.ketha.FoE_RoomReservation.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ketha.FoE_RoomReservation.dto.ResponseDto;
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
import com.ketha.FoE_RoomReservation.service.impl.BookingServiceImpl;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private RoomRepository roomRepository;

	@Mock
	private BookingRepository bookingRepository;

	@Mock
	private EventRepository eventRepository;

	@InjectMocks
	private BookingServiceImpl bookingService;

	private User user;
	private User admin;
	private Room room;
	private Booking bookingRequest,booking1,booking2;

	@BeforeEach
	void setUp() {
		user = User.builder()
				.firstName("David")
				.lastName("John")
				.email("e20009@eng.pdn.ac.lk")
				.phoneNo(0771233456)
				.userName("David")
				.password("password")
				.userType(UserType.regularUser)
				.build();
		
		admin = User.builder()
				.firstName("David")
				.lastName("John")
				.email("e20009@eng.pdn.ac.lk")
				.phoneNo(0771233456)
				.userName("David")
				.password("password")
				.userType(UserType.admin)
				.build();

		room = new Room();
		room.setRoomId(1);
		room.setRoomName("lecture hall 1");

		bookingRequest = new Booking();
		bookingRequest.setStartTime(Time.valueOf("08:00:00"));
		bookingRequest.setEndTime(Time.valueOf("09:00:00"));
		bookingRequest.setDate(Date.valueOf("2024-09-11"));
		bookingRequest.setRecurrence(RecurrenceType.none);
		bookingRequest.setRecurrencePeriod(1);
		
		Event event1 = Event.builder()
				.eventId(1L)
				.build();
		
		Event event2 = Event.builder()
				.eventId(2L)
				.build();
		
		booking1 = Booking.builder()
				.bookingId(1)
				.startTime(Time.valueOf("08:00:00"))
				.endTime(Time.valueOf("09:00:00"))
				.date(Date.valueOf("2024-09-10"))
				.recurrence(RecurrenceType.none)
				.recurrencePeriod(1)
				.event(event1)
				.room(room)
				.user(user)
				.build();
		
		booking2 = Booking.builder()
				.bookingId(2)
				.startTime(Time.valueOf("08:00:00"))
				.endTime(Time.valueOf("09:00:00"))
				.date(Date.valueOf("2024-09-09"))
				.recurrence(RecurrenceType.none)
				.event(event2)
				.user(user)
				.room(room)
				.recurrencePeriod(1)
				.build();
		
//		room.setBookings(Arrays.asList(booking1,booking2));
	}

	@Test
	void BookingService_AddBookingByUser_ReturnBooking() throws Exception {
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(roomRepository.findByRoomName(room.getRoomName())).thenReturn(Optional.of(room));
		when(bookingRepository.save(any(Booking.class))).thenReturn(bookingRequest);
		when(eventRepository.save(any(Event.class))).thenReturn(new Event());

		ResponseDto response = bookingService.addBooking(1L, room.getRoomName(), bookingRequest);

		verify(bookingRepository).save(any(Booking.class));
		verify(eventRepository).save(any(Event.class));

		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
		Assertions.assertThat(response.getMessage()).isEqualTo("Successful");
	}
	
	@Test
	void BookingService_AddBookingByUser_RecurrenceBooking() throws Exception {
		bookingRequest.setRecurrence(RecurrenceType.daily);
		bookingRequest.setRecurrencePeriod(3);
		
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(roomRepository.findByRoomName(room.getRoomName())).thenReturn(Optional.of(room));
		when(bookingRepository.save(any(Booking.class))).thenReturn(bookingRequest);
		when(eventRepository.save(any(Event.class))).thenReturn(new Event());

		ResponseDto response = bookingService.addBooking(1L, room.getRoomName(), bookingRequest);

		verify(bookingRepository,times(3)).save(any(Booking.class));
		verify(eventRepository).save(any(Event.class));

		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
		Assertions.assertThat(response.getMessage()).isEqualTo("Successful");
	}
	
	@Test
	void BookingService_AddBookingByUser_MoreThanThreeRecurrenceBookingsNotAllowed() throws Exception {
		bookingRequest.setRecurrence(RecurrenceType.daily);
		bookingRequest.setRecurrencePeriod(4);
		
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(roomRepository.findByRoomName(room.getRoomName())).thenReturn(Optional.of(room));

		ResponseDto response = bookingService.addBooking(1L, room.getRoomName(), bookingRequest);

		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(403);
		Assertions.assertThat(response.getMessage()).isEqualTo("Forbidden: not allowed to book with these specification");
	}

	@Test
	void BookingService_AddBookingByUser_RoomNotAvailable() throws Exception {
		List<Booking> existingBookings = new ArrayList<>();
		Booking existingBooking = new Booking();
		existingBooking.setStartTime(Time.valueOf("08:00:00"));
		existingBooking.setEndTime(Time.valueOf("09:00:00"));
		existingBooking.setDate(Date.valueOf("2024-09-11"));
		existingBookings.add(existingBooking);
		
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(roomRepository.findByRoomName(room.getRoomName())).thenReturn(Optional.of(room));
		
		room.setBookings(existingBookings);
		
		ResponseDto response = bookingService.addBooking(1L, room.getRoomName(), bookingRequest);

		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(404);
		Assertions.assertThat(response.getMessage()).isEqualTo("Rooms are not available for those selected dates");
	}
	
	@Test
	void BookingService_AddBookingByUser_NotAllowedBookingWeekends() {
		// Saturday
		bookingRequest.setDate(Date.valueOf("2024-09-14"));
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(roomRepository.findByRoomName(room.getRoomName())).thenReturn(Optional.of(room));
		
		ResponseDto response = bookingService.addBooking(1L, room.getRoomName(), bookingRequest);
		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(403);
		Assertions.assertThat(response.getMessage()).isEqualTo("Forbidden: not allowed to book with these specification");
	}
	
	@Test
	void BookingService_AddBookingByUser_NotAllowedBookingBeforeEight() {
		
		bookingRequest.setStartTime(Time.valueOf("07:59:00"));
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(roomRepository.findByRoomName(room.getRoomName())).thenReturn(Optional.of(room));
		
		ResponseDto response = bookingService.addBooking(1L, room.getRoomName(), bookingRequest);
		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(403);
		Assertions.assertThat(response.getMessage()).isEqualTo("Forbidden: not allowed to book with these specification");
	}
	
	@Test
	void BookingService_AddBookingByUser_NotAllowedBookingAfterFive() {
		
		bookingRequest.setEndTime(Time.valueOf("17:01:00"));
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(roomRepository.findByRoomName(room.getRoomName())).thenReturn(Optional.of(room));
		
		ResponseDto response = bookingService.addBooking(1L, room.getRoomName(), bookingRequest);
		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(403);
		Assertions.assertThat(response.getMessage()).isEqualTo("Forbidden: not allowed to book with these specification");
	}
	
	@Test
	void BookingService_AddBookingByAdmin_ReturnBooking() throws Exception {
		when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
		when(roomRepository.findByRoomName(room.getRoomName())).thenReturn(Optional.of(room));
		when(bookingRepository.save(any(Booking.class))).thenReturn(bookingRequest);
		when(eventRepository.save(any(Event.class))).thenReturn(new Event());

		ResponseDto response = bookingService.addBooking(1L, room.getRoomName(), bookingRequest);

		verify(bookingRepository).save(any(Booking.class));
		verify(eventRepository).save(any(Event.class));

		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
		Assertions.assertThat(response.getMessage()).isEqualTo("Successful");
	}
	
	@Test
	void BookingService_AddBookingByAdmin_RecurrenceBooking() throws Exception {
		bookingRequest.setRecurrence(RecurrenceType.daily);
		bookingRequest.setRecurrencePeriod(3);
		
		when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
		when(roomRepository.findByRoomName(room.getRoomName())).thenReturn(Optional.of(room));
		when(bookingRepository.save(any(Booking.class))).thenReturn(bookingRequest);
		when(eventRepository.save(any(Event.class))).thenReturn(new Event());

		ResponseDto response = bookingService.addBooking(1L, room.getRoomName(), bookingRequest);

		verify(bookingRepository,times(3)).save(any(Booking.class));
		verify(eventRepository).save(any(Event.class));

		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
		Assertions.assertThat(response.getMessage()).isEqualTo("Successful");
	}
	
	@Test
	void BookingService_AddBookingByAdmin_MoreThanSevenRecurrenceBookingsNotAllowed() throws Exception {
		bookingRequest.setRecurrence(RecurrenceType.daily);
		bookingRequest.setRecurrencePeriod(8);
		
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(roomRepository.findByRoomName(room.getRoomName())).thenReturn(Optional.of(room));

		ResponseDto response = bookingService.addBooking(1L, room.getRoomName(), bookingRequest);

		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(403);
		Assertions.assertThat(response.getMessage()).isEqualTo("Forbidden: not allowed to book with these specification");
	}
	
	@Test
	void BookingService_AddBookingByAdmin_RecurrenceBookingWeekly() throws Exception {
		bookingRequest.setRecurrence(RecurrenceType.weekly);
		bookingRequest.setRecurrencePeriod(3);
		
		when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
		when(roomRepository.findByRoomName(room.getRoomName())).thenReturn(Optional.of(room));
		when(bookingRepository.save(any(Booking.class))).thenReturn(bookingRequest);
		when(eventRepository.save(any(Event.class))).thenReturn(new Event());

		ResponseDto response = bookingService.addBooking(1L, room.getRoomName(), bookingRequest);

		verify(bookingRepository,times(3)).save(any(Booking.class));
		verify(eventRepository).save(any(Event.class));

		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
		Assertions.assertThat(response.getMessage()).isEqualTo("Successful");
	}
	
	@Test
	void BookingService_AddBookingByAdmin_MoreThanSixteenWeeklyRecurrenceBookingsNotAllowed() throws Exception {
		bookingRequest.setRecurrence(RecurrenceType.weekly);
		bookingRequest.setRecurrencePeriod(17);
		
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(roomRepository.findByRoomName(room.getRoomName())).thenReturn(Optional.of(room));

		ResponseDto response = bookingService.addBooking(1L, room.getRoomName(), bookingRequest);

		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(403);
		Assertions.assertThat(response.getMessage()).isEqualTo("Forbidden: not allowed to book with these specification");
	}
	
	@Test
	void BookingService_AddBooking_UserNotFound() throws Exception {
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		ResponseDto response = bookingService.addBooking(1L, room.getRoomName(), bookingRequest);

		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(404);
		Assertions.assertThat(response.getMessage()).isEqualTo("User not found");
	}

	@Test
	void BookingService_AddBooking_RooomNotFound() throws Exception {
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(roomRepository.findByRoomName(room.getRoomName())).thenReturn(Optional.empty());

		ResponseDto response = bookingService.addBooking(1L, room.getRoomName(), bookingRequest);

		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(404);
		Assertions.assertThat(response.getMessage()).isEqualTo("Room not found");
	}
	
	@Test
	void BookingService_GetBooking_ReturnAllBookings() {
		when(bookingRepository.findAll()).thenReturn(Arrays.asList(booking1,booking2));
		
		ResponseDto response = bookingService.getAllbookings();
		
		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
		Assertions.assertThat(response.getMessage()).isEqualTo("Successful");
		Assertions.assertThat(response.getBookingList()).isNotNull();
	}
	
	@Test
	void BookingService_GetBooking_ReturnBookingById() {
		when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking1));
		
		ResponseDto response = bookingService.getBookingById(1L);
		
		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
		Assertions.assertThat(response.getMessage()).isEqualTo("Successful");
		Assertions.assertThat(response.getBooking()).isNotNull();
	}
	
	@Test
	void BookingService_GetBookingByDate_ReturnBookings() {
		when(bookingRepository.findBookingByDate(Mockito.any(Date.class))).thenReturn(Arrays.asList(booking1,booking2));
		
		ResponseDto response = bookingService.getBookingByDate(booking1.getDate());
		
		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
		Assertions.assertThat(response.getMessage()).isEqualTo("Successful");
		Assertions.assertThat(response.getBookingList()).isNotNull();
	}
	
	@Test
	void BookingService_CancelBooking_DeleteBookingNoReccurence() {
		when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking1));
		when(bookingRepository.findAllByEventId(1L)).thenReturn(Arrays.asList(booking1));
		
		ResponseDto response = bookingService.cancelBooking(1L);
		
		verify(bookingRepository).deleteAllById(Mockito.anyList());
		verify(eventRepository).deleteById(Mockito.any(Long.class));
		
		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
		Assertions.assertThat(response.getMessage()).isEqualTo("Successful");
		Assertions.assertThat(response.getBookingList()).isNotNull();
	}
	
	@Test
	void BookingService_CancelBooking_DeleteBookingWithReccurence() {
		
		when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking1));
		
		// booking 1 and booking 2 has same event Id
		when(bookingRepository.findAllByEventId(1L)).thenReturn(Arrays.asList(booking1,booking2));
		
		ResponseDto response = bookingService.cancelBooking(1L);
		
		verify(bookingRepository).deleteAllById(Mockito.anyList());
		verify(eventRepository).deleteById(Mockito.any(Long.class));
		
		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
		Assertions.assertThat(response.getMessage()).isEqualTo("Successful");
		Assertions.assertThat(response.getBookingList()).isNotNull();
	}
}
