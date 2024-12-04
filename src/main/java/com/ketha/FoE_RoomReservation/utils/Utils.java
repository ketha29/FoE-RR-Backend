package com.ketha.FoE_RoomReservation.utils;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ketha.FoE_RoomReservation.dto.BookingDto;
import com.ketha.FoE_RoomReservation.dto.RoomDto;
import com.ketha.FoE_RoomReservation.dto.UserDto;
import com.ketha.FoE_RoomReservation.model.Booking;
import com.ketha.FoE_RoomReservation.model.Room;
import com.ketha.FoE_RoomReservation.model.User;

public class Utils {

	public static UserDto mapUserToUserDto(User user) {
		UserDto userDto = new UserDto();

		userDto.setUserId(user.getUserId());
		userDto.setEmail(user.getEmail());
		userDto.setPhoneNo(user.getPhoneNo());
		userDto.setUserName(user.getUserName());
		userDto.setUserType(user.getUserType());
		userDto.setFirstName(user.getFirstName());
		userDto.setLastName(user.getLastName());
		return userDto;
	}

	public static RoomDto mapRoomToRoomDto(Room room) {
		RoomDto roomDto = new RoomDto();

		roomDto.setRoomId(room.getRoomId());
		roomDto.setRoomName(room.getRoomName());
		roomDto.setCapacity(room.getCapacity());
		roomDto.setDescription(room.getDescription());
		return roomDto;
	}

	public static BookingDto mapBookingToBookingDto(Booking booking) {
		BookingDto bookingDto = new BookingDto();

		bookingDto.setBookingId(booking.getBookingId());
		bookingDto.setStartTime(booking.getStartTime());
		bookingDto.setEndTime(booking.getEndTime());
		bookingDto.setDate(booking.getDate());
		bookingDto.setStartDate(booking.getStartDate());
		bookingDto.setEndDate(booking.getEndDate());
		bookingDto.setRecurrence(booking.getRecurrence());
		bookingDto.setRecurrencePeriod(booking.getRecurrencePeriod());
		bookingDto.setDetails(booking.getDetails());
		return bookingDto;
	}

	public static RoomDto mapRoomToRoomDtoPlusBookings(Room room) {
		RoomDto roomDto = new RoomDto();

		roomDto.setRoomId(room.getRoomId());
		roomDto.setRoomName(room.getRoomName());
		roomDto.setCapacity(room.getCapacity());
		roomDto.setDescription(room.getDescription());
		if (!room.getBookings().isEmpty()) {
			roomDto.setBookings(room.getBookings().stream().map(booking -> mapBookingToBookingDto(booking))
					.collect(Collectors.toList()));
		}
		return roomDto;
	}

	public static BookingDto mapBookingToBookingDtoPlusBookedRooms(Booking booking, boolean mapUser) {
		BookingDto bookingDto = new BookingDto();

		bookingDto.setBookingId(booking.getBookingId());
		bookingDto.setStartTime(booking.getStartTime());
		bookingDto.setEndTime(booking.getEndTime());
		bookingDto.setDate(booking.getDate());
		bookingDto.setStartDate(booking.getStartDate());
		bookingDto.setEndDate(booking.getEndDate());
		bookingDto.setRecurrence(booking.getRecurrence());
		bookingDto.setRecurrencePeriod(booking.getRecurrencePeriod());
		bookingDto.setDetails(booking.getDetails());
		if (mapUser) {
			bookingDto.setUser(Utils.mapUserToUserDto(booking.getUser()));
		}
		if (booking.getRoom() != null) {
			RoomDto roomDto = new RoomDto();

			roomDto.setRoomId(booking.getRoom().getRoomId());
			roomDto.setRoomName(booking.getRoom().getRoomName());
			roomDto.setCapacity(booking.getRoom().getCapacity());
			roomDto.setDescription(booking.getRoom().getDescription());
			bookingDto.setRoom(roomDto);
		}
		return bookingDto;
	}

	public static UserDto mapUserToUserEntityPlusBookings(User user) {
		UserDto userDto = new UserDto();

		userDto.setUserId(user.getUserId());
		userDto.setEmail(user.getEmail());
		userDto.setPhoneNo(user.getPhoneNo());
		userDto.setUserName(user.getUserName());
		userDto.setUserType(user.getUserType());

		if (!user.getBookings().isEmpty()) {
			userDto.setBookings(
					user.getBookings().stream().map(booking -> mapBookingToBookingDtoPlusBookedRooms(booking, false))
							.collect(Collectors.toList()));
		}
		return userDto;
	}

	public static List<UserDto> mapUserListToUserListDto(List<User> userList) {
		return userList.stream().map(user -> mapUserToUserDto(user)).collect(Collectors.toList());
	}

	public static List<RoomDto> mapRoomListToRoomListDto(List<Room> roomList) {
		return roomList.stream().map(room -> mapRoomToRoomDto(room)).collect(Collectors.toList());
	}

	public static List<BookingDto> mapBookingListToBookingListDto(List<Booking> bookingList) {
		return bookingList.stream().map(booking -> mapBookingToBookingDtoPlusBookedRooms(booking, true))
				.collect(Collectors.toList());
	}

	// Format time for notification
	public static String formatTime(Time time) {
		SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
		return sdf.format(time);
	};

	// Format Date for notification
	public static List<String> formatDateList(List<Date> availableDateList) {

		List<String> dates = new ArrayList<String>();

		for (Date date : availableDateList) {
			SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy, EEE");
			dates.add(sdf.format(date));
		}
		return dates;
	}
}
