package com.ketha.FoE_RoomReservation.service.impl;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.ketha.FoE_RoomReservation.dto.MailRequestDto;
import com.ketha.FoE_RoomReservation.model.User;
import com.ketha.FoE_RoomReservation.model.User.UserType;
import com.ketha.FoE_RoomReservation.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl {

	@Value("${spring.mail.username}")
	private String sender;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private SpringTemplateEngine templateEngine;

	@Autowired
	private UserRepository userRepository;
	
	@Async
	public void postEmail(MailRequestDto request, EmailType emailType) throws MessagingException {

		Context context = new Context();
		context.setVariable("request", request);

		MimeMessage message = mailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());

		helper.setFrom(sender);
		if (emailType == EmailType.notifyAdmin) {
			helper.setTo(getAdminMail());
		} else {
			helper.setTo(request.getTo());
		}

		helper.setSubject(request.getSubject());

		switch (emailType) {
		case placeBooking:
			// booking placed mail
			helper.setText(templateEngine.process("booking-success", context), true);
			break;
		case userCancelBooking:
			// cancel booking mail
			helper.setText(templateEngine.process("booking-cancelled", context), true);

			break;
		case notifyAdmin:
			// Notify admin when user cancelled booking
			helper.setText(templateEngine.process("user-cancelled-booking", context), true);

			break;
		case notifyUser:
			// Notify relevant user when admin cancelled booking
			helper.setText(templateEngine.process("Admin-cancelled-booking", context), true);

			break;
		default:
			break;
		}

		mailSender.send(message);
	}

	public enum EmailType {
		placeBooking, userCancelBooking, notifyAdmin, notifyUser
	}

	public List<String> formatDateList(List<Date> availableDateList) {

		List<String> dates = new ArrayList<String>();

		for (Date date : availableDateList) {
			SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy, EEE");
			dates.add(sdf.format(date));
		}
		return dates;
	}

	public String formatTime(Time time) {
		SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
		return sdf.format(time);
	};

	private String[] getAdminMail() {

		List<User> admins = userRepository.findUserByUserType(UserType.admin);
		String[] adminMailList = admins.stream().map(admin -> admin.getEmail()).toArray(String[]::new);

		return adminMailList;
	}
}
