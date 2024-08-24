package com.ketha.FoE_RoomReservation.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ketha.FoE_RoomReservation.model.Booking;
import com.ketha.FoE_RoomReservation.model.Event;
import com.ketha.FoE_RoomReservation.model.Room;
import com.ketha.FoE_RoomReservation.model.User;
import com.ketha.FoE_RoomReservation.model.User.UserType;
import com.ketha.FoE_RoomReservation.model.Booking.RecurrenceType;
import com.ketha.FoE_RoomReservation.repository.BookingRepository;
import com.ketha.FoE_RoomReservation.repository.EventRepository;
import com.ketha.FoE_RoomReservation.repository.RoomRepository;
import com.ketha.FoE_RoomReservation.repository.UserRepository;
import com.ketha.FoE_RoomReservation.service.impl.BookingServiceImpl;
import com.ketha.FoE_RoomReservation.dto.ResponseDto;

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
    private Room room;
    private Booking bookingRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1);
        user.setUserType(UserType.regularUser);

        room = new Room();
        room.setRoomId(1);

        bookingRequest = new Booking();
        bookingRequest.setStartTime(Time.valueOf("08:00:00"));
        bookingRequest.setEndTime(Time.valueOf("09:00:00"));
        bookingRequest.setDate(Date.valueOf("2024-09-11"));
        bookingRequest.setRecurrence(RecurrenceType.none);
        bookingRequest.setRecurrencePeriod(1);
    }

    @Test
    void BookingService_AddBooking_RetuenBooking() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingRequest);
        when(eventRepository.save(any(Event.class))).thenReturn(new Event());

        ResponseDto response = bookingService.addBooking(1L, 1L, bookingRequest);

        verify(bookingRepository).save(any(Booking.class));
        verify(eventRepository).save(any(Event.class));

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
        Assertions.assertThat(response.getMessage()).isEqualTo("Successful");
    }

    @Test
    void BookingService_AddBooking_RoomNotAvailable() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        List<Booking> existingBookings = new ArrayList<>();
        Booking existingBooking = new Booking();
        existingBooking.setStartTime(Time.valueOf("08:00:00"));
        existingBooking.setEndTime(Time.valueOf("09:00:00"));
        existingBooking.setDate(Date.valueOf("2024-09-11"));
        existingBookings.add(existingBooking);

        room.setBookings(existingBookings);

        ResponseDto response = bookingService.addBooking(1L, 1L, bookingRequest);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(404);
        Assertions.assertThat(response.getMessage()).isEqualTo("Rooms are not available for those selected dates");
    }

    @Test
    void BookingService_AddBooking_UserNotFound() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseDto response = bookingService.addBooking(1L, 1L, bookingRequest);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(404);
        Assertions.assertThat(response.getMessage()).isEqualTo("User not found");
    }

    @Test
    void BookingService_AddBooking_RooomNotFound() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseDto response = bookingService.addBooking(1L, 1L, bookingRequest);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(404);
        Assertions.assertThat(response.getMessage()).isEqualTo("Room not found");
    }
}
