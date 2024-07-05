package com.ketha.FoE_RoomReservation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@RequestMapping("/home")
	public String greet() {
		return "Welcome to FoE room reservation";
	}
	
	@RequestMapping("/regularUser/home")
	public String regularUserGreet() {
		return "Hey, regular user welcome to FoE room reservation";
	}
	
	@RequestMapping("/admin/home")
	public String adminGreet() {
		return "Hey, admin welcome to FoE room reservation";
	}
	
	@RequestMapping("/superAdmin/home")
	public String superAdminGreet() {
		return "Hey, super admin welcome to FoE room reservation";
	}
}
