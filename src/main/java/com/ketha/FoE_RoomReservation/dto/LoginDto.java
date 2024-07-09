package com.ketha.FoE_RoomReservation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginDto {

	@NotBlank(message = "user name is required")
	private String userName;
	@NotBlank(message = "password name is required")
	private String password;
}
