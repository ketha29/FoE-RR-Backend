package com.ketha.FoE_RoomReservation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChangePasswordDto {
	private String currentPassword;
	private String newPassWord;
	private String confirmPassword;
	
}
