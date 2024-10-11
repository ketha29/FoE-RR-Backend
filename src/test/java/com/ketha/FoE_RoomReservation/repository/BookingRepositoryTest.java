package com.ketha.FoE_RoomReservation.repository;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ketha.FoE_RoomReservation.model.Booking;
import com.ketha.FoE_RoomReservation.model.Booking.RecurrenceType;
import com.ketha.FoE_RoomReservation.model.Event;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class BookingRepositoryTest {

	@Autowired
	BookingRepository bookingRepository;
	
	@Autowired
	EventRepository eventRepository; 
	
	@Test
	public void BookingRepository_FindById_ReturnBooking() {
		// Arrange
		Booking booking = Booking.builder()
							.startTime(Time.valueOf("08:00:00"))
							.endTime(Time.valueOf("09:00:00"))
							.date(Date.valueOf("2024-09-11"))
							.recurrence(RecurrenceType.none)
							.recurrencePeriod(1)
							.build();
		
		bookingRepository.save(booking);
		
		// Act
		Booking returnBooking = bookingRepository.findById(booking.getBookingId()).get();
		
		// Assert
		Assertions.assertThat(returnBooking).isNotNull();
	}
	
	@Test
	public void BookingRepository_FindAll_ReturnBookingList() {
		// Arrange
		Booking booking1 = Booking.builder()
							.startTime(Time.valueOf("08:00:00"))
							.endTime(Time.valueOf("09:00:00"))
							.date(Date.valueOf("2024-09-11"))
							.recurrence(RecurrenceType.none)
							.recurrencePeriod(1)
							.build();
		
		Booking booking2 = Booking.builder()
				.startTime(Time.valueOf("10:00:00"))
				.endTime(Time.valueOf("11:00:00"))
				.date(Date.valueOf("2024-09-11"))
				.recurrence(RecurrenceType.none)
				.recurrencePeriod(1)
				.build();
		
		bookingRepository.save(booking1);
		bookingRepository.save(booking2);
		
		// Act
		List<Booking> bookingList = bookingRepository.findAll();
		
		// Assert
		Assertions.assertThat(bookingList).isNotNull();
		Assertions.assertThat(bookingList.size()).isEqualTo(2);
	}
	
	@Test
	public void BookingRepository_FindByEventId_ReturnBookingList() {
		// Arrange
		Event event1 = Event.builder().eventId(1).build();
		
		Booking booking1 = Booking.builder()
				.startTime(Time.valueOf("08:00:00"))
				.endTime(Time.valueOf("09:00:00"))
				.date(Date.valueOf("2024-09-11"))
				.recurrence(RecurrenceType.none)
				.recurrencePeriod(1)
				.event(event1)
				.build();

		Booking booking2 = Booking.builder()
			.startTime(Time.valueOf("10:00:00"))
			.endTime(Time.valueOf("11:00:00"))
			.date(Date.valueOf("2024-09-11"))
			.recurrence(RecurrenceType.none)
			.recurrencePeriod(1)
			.event(event1)
			.build();
		
		eventRepository.save(event1);
		bookingRepository.save(booking1);
		bookingRepository.save(booking2);
		
		// Act
		List<Booking> bookingList = bookingRepository.findAllByEventId(event1.getEventId());
		
		// Assert
		Assertions.assertThat(bookingList).isNotNull();
		Assertions.assertThat(bookingList.size()).isEqualTo(2);
	}
	
	@Test
	public void BookingRepository_FindByDate_ReturnBookingList() {
		// Arrange
		Booking booking1 = Booking.builder()
				.startTime(Time.valueOf("08:00:00"))
				.endTime(Time.valueOf("09:00:00"))
				.date(Date.valueOf("2024-09-11"))
				.recurrence(RecurrenceType.none)
				.recurrencePeriod(1)
				.build();

		Booking booking2 = Booking.builder()
			.startTime(Time.valueOf("10:00:00"))
			.endTime(Time.valueOf("11:00:00"))
			.date(Date.valueOf("2024-09-11"))
			.recurrence(RecurrenceType.none)
			.recurrencePeriod(1)
			.build();
		
		bookingRepository.save(booking1);
		bookingRepository.save(booking2);
		
		// Act
		List<Booking> bookingList = bookingRepository.findBookingByDate(Date.valueOf("2024-09-11"));
		
		// Assert
		Assertions.assertThat(bookingList).isNotNull();
		Assertions.assertThat(bookingList.size()).isEqualTo(2);
	}
}
