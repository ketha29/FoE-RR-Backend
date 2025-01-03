package com.ketha.FoE_RoomReservation.service.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ketha.FoE_RoomReservation.dto.NotificationDto;
import com.ketha.FoE_RoomReservation.service.interfac.NotificationService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl {
	
	private final SuccessNotificationServiceImpl successNotificationService;
	private final CancelledNotificationImpl cancelNotificationService;

	@Value("${spring.mail.username}")
	private String sender;

	@Autowired
	private JavaMailSender mailSender;

	private NotificationService service;
	
	@Autowired
	public EmailServiceImpl(SuccessNotificationServiceImpl successNotificationService, CancelledNotificationImpl cancelNotificationService) {
		this.successNotificationService = successNotificationService;
		this.cancelNotificationService = cancelNotificationService;
	}

	@Async
	public void postEmail(NotificationDto notificationDto, Boolean isSuccess) throws MessagingException {

		selectNotificationService(isSuccess);
		
		service.setNotification(notificationDto);

		// Post email to logged user
		MimeMessage message1 = mailSender.createMimeMessage();
		MimeMessageHelper helper1 = new MimeMessageHelper(message1, StandardCharsets.UTF_8.name());
		helper1.setFrom(sender);
		helper1.setTo(service.getLoggedUserMail());
		List<String> loggedUserMessage = service.getLoggedUserMessage();
		helper1.setSubject(loggedUserMessage.getFirst());
		helper1.setText(loggedUserMessage.getLast(), true);
		sendEmail(message1);

		if (service.getOtherUsersMail() != null) {
			// Post email to Other user/users
			MimeMessage message2 = mailSender.createMimeMessage();
			MimeMessageHelper helper2 = new MimeMessageHelper(message2, StandardCharsets.UTF_8.name());
			helper2.setFrom(sender);
			helper2.setTo(service.getOtherUsersMail());
			
			// TODO format email template
			List<String> otherUserMessage = service.getOtherUserMessage();
			helper2.setSubject(otherUserMessage.getFirst());
			helper2.setText(otherUserMessage.getLast(), true);
			sendEmail(message2);
		}
	}

	private void sendEmail(MimeMessage message) throws MessagingException {
		mailSender.send(message);
	}
	
	private void selectNotificationService(boolean isSuccess) {
		service = (isSuccess) ? successNotificationService : cancelNotificationService;
	}
}
