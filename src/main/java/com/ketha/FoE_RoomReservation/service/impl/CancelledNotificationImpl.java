package com.ketha.FoE_RoomReservation.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.ketha.FoE_RoomReservation.dto.NotificationDto;
import com.ketha.FoE_RoomReservation.dto.UserDto;
import com.ketha.FoE_RoomReservation.model.User;
import com.ketha.FoE_RoomReservation.model.User.UserType;
import com.ketha.FoE_RoomReservation.repository.UserRepository;
import com.ketha.FoE_RoomReservation.service.interfac.NotificationService;

@Service
public class CancelledNotificationImpl implements NotificationService {

	private final SpringTemplateEngine templateEngine;
	
	private final UserRepository userRepository;


	private NotificationDto notificationDto;

	private UserDto loggedUser, otherUser;


	@Autowired
	public CancelledNotificationImpl(SpringTemplateEngine templateEngine, UserRepository userRepository) {
		this.templateEngine = templateEngine;
		this.userRepository = userRepository;
	}
	
	@Override
	public NotificationDto getNotification() {
		return notificationDto;
	}

	@Override
	public void setNotification(NotificationDto notificationDto) {
		this.notificationDto = notificationDto;
		UserDto bookForUser = notificationDto.getBookForUser();
		UserDto bookByUser	= (notificationDto.getBookedByUser().getUserType() == UserType.regularUser)? notificationDto.getBookedByUser():null;
		loggedUser = notificationDto.getLoggedUser();

		if (loggedUser.getUserType() == UserType.admin) {
			otherUser = (bookForUser != null) ? bookForUser : bookByUser;
		} 
	}

	// Confirmation mail to Logged user
	@Override
	public List<String> getLoggedUserMessage() {

		List<String> notifyLoggedUser = new ArrayList<String>();
		notifyLoggedUser.add("Booking cancelled : FOE Room Reservation");
		UserDto loggedUser = notificationDto.getLoggedUser();
		
		Context context = new Context();
		context.setVariable("notification", notificationDto);
		context.setVariable("userName", loggedUser.getUserName());
		notifyLoggedUser.add(templateEngine.process("booking-cancelled", context));
		return notifyLoggedUser;
	}

	// Cancelled Notification mail to booked for user when admin cancelled booking
	@Override
	public List<String> getOtherUserMessage() {
		if (loggedUser.getUserType() == UserType.admin || loggedUser.getUserType() == UserType.superAdmin) {
			return notifyRegularUser();
		} else {
			return notifyAdminUser();
		}
	}

	private List<String> notifyRegularUser() {
		List<String> notifyRegularUser = new ArrayList<String>();
		notifyRegularUser.add("Admin cancelled booking : FOE Room Reservation");

		Context context = new Context();
		context.setVariable("notification", notificationDto);
		context.setVariable("userName", otherUser.getUserName());
		notifyRegularUser.add(templateEngine.process("Admin-cancelled-booking", context));

		return notifyRegularUser;
	}


	private List<String> notifyAdminUser() {
		List<String> notifyAdminUser = new ArrayList<String>();
		notifyAdminUser.add("User cancelled booking : FOE Room Reservation");

		Context context = new Context();
		context.setVariable("notification", notificationDto);
		context.setVariable("userName", loggedUser.getUserName());
		notifyAdminUser.add(templateEngine.process("user-cancelled-booking", context));
		return notifyAdminUser;
	}

	@Override
	public String getLoggedUserMail() {
		return loggedUser.getEmail();
	}

	@Override
	public String[] getOtherUsersMail() {
		String[] otherUsersMail = null;

		if ((loggedUser.getUserType() == UserType.admin || loggedUser.getUserType() == UserType.superAdmin)  && (otherUser != null)) {
			// Obtain Booked for Users mail address
			String[] userMail = { otherUser.getEmail() };
			otherUsersMail = userMail;
			
		} else if (loggedUser.getUserType() == UserType.regularUser) {
			otherUsersMail = getAdminMail();
		}
		return otherUsersMail;
	}

	private String[] getAdminMail() {

		List<User> admins = userRepository.findUserByUserType(UserType.admin);
		String[] adminMailList = admins.stream().map(admin -> admin.getEmail()).toArray(String[]::new);

		return adminMailList;
	}

}
