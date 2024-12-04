package com.ketha.FoE_RoomReservation.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.ketha.FoE_RoomReservation.dto.NotificationDto;
import com.ketha.FoE_RoomReservation.dto.UserDto;
import com.ketha.FoE_RoomReservation.model.User.UserType;
import com.ketha.FoE_RoomReservation.service.interfac.NotificationService;

@Service
public class SuccessNotificationServiceImpl implements NotificationService {

	private final SpringTemplateEngine templateEngine;

	private NotificationDto notificationDto;

	private UserDto loggedUser, otherUser;

	@Autowired
	public SuccessNotificationServiceImpl(SpringTemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	@Override
	public NotificationDto getNotification() {
		return notificationDto;
	}

	@Override
	public void setNotification(NotificationDto notificationDto) {
		this.notificationDto = notificationDto;

		UserDto bookForUser = notificationDto.getBookForUser();

		loggedUser = notificationDto.getLoggedUser();

		if (loggedUser.getUserType() == UserType.admin) {
			otherUser = (bookForUser != null) ? bookForUser : null;
		}
	}

	// Confirmation mail to Logged user
	@Override
	public List<String> getLoggedUserMessage() {

		List<String> notifyLoggedUser = new ArrayList<String>();
		notifyLoggedUser.add("Booking placed : FOE Room Reservation");
		UserDto loggedUser = notificationDto.getLoggedUser();

		Context context = new Context();
		context.setVariable("notification", notificationDto);
		context.setVariable("userName", loggedUser.getUserName());
		notifyLoggedUser.add(templateEngine.process("booking-success", context));
		return notifyLoggedUser;
	}

	// Confirmation mail to booked for user when admin place booking
	@Override
	public List<String> getOtherUserMessage() {
		if (loggedUser.getUserType() == UserType.admin || loggedUser.getUserType() == UserType.superAdmin) {
			return notifyRegularUser();
		}
		// TODO if we need to inform admin implement here
		return null;
	}

	private List<String> notifyRegularUser() {
		List<String> notifyRegularUser = new ArrayList<String>();
		notifyRegularUser.add("Booking placed on behalf, by Admin: FOE Room Reservation");

		Context context = new Context();
		context.setVariable("notification", notificationDto);
		context.setVariable("userName", otherUser.getUserName());
		// TODO change the mail format
		notifyRegularUser.add(templateEngine.process("book-for-user-booking-success.html", context));

		return notifyRegularUser;
	}

	// TODO is the admin required to be informed about all bookings??

//	private List<String> notifyAdminUser() {
//		List<String> notifyAdminUser = new ArrayList<String>();
//		notifyAdminUser.add("User cancelled booking : FOE Room Reservation");
//		
//		Context context = new Context();
//		context.setVariable("request", request);
//		notifyAdminUser.add(templateEngine.process("user-cancelled-booking", context));
//		
//		return notifyAdminUser;
//	}

	@Override
	public String getLoggedUserMail() {
		return loggedUser.getEmail();
	}

	@Override
	public String[] getOtherUsersMail() {
		String[] otherUsersMail = null;
		if ((loggedUser.getUserType() == UserType.admin || loggedUser.getUserType() == UserType.superAdmin)
				&& (otherUser != null)) {
			String[] userMail = { otherUser.getEmail() };
			otherUsersMail = userMail;
		}
		return otherUsersMail;
	}

	// TODO do we need to inform Admins?
//	private String[] getAdminMail() {
//
//		List<User> admins = userRepository.findUserByUserType(UserType.admin);
//		String[] adminMailList = admins.stream().map(admin -> admin.getEmail()).toArray(String[]::new);
//
//		return adminMailList;
//	}	
}
