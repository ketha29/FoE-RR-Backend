package com.ketha.FoE_RoomReservation.email.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailResponseDto {
	
	private String message;
	private int statusCode;
}
