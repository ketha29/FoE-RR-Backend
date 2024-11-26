package com.ketha.FoE_RoomReservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FoERoomReservationApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoERoomReservationApplication.class, args);
	}

}
