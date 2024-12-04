package com.ketha.FoE_RoomReservation.service.interfac;

import com.ketha.FoE_RoomReservation.dto.NotificationDto;

import jakarta.mail.MessagingException;

public interface EmailService {

	void postEmail(NotificationDto notificationDto) throws MessagingException;
	
	
}
