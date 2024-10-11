package com.ketha.FoE_RoomReservation.email.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MailRequestDto {
	
	private String userName;
	private String to;
	private String subject;
	
}
