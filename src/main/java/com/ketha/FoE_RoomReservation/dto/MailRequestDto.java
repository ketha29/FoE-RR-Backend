package com.ketha.FoE_RoomReservation.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MailRequestDto {
	
	private String to;
	private String subject;
	private String userName;
	private String purpose;
	private List<String> dates;
	private String startTime;
	private String endTime;
	private String roomName;
	
}
