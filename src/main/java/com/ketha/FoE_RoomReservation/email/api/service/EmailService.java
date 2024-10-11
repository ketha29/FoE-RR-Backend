package com.ketha.FoE_RoomReservation.email.api.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.ketha.FoE_RoomReservation.email.api.dto.MailRequestDto;
import com.ketha.FoE_RoomReservation.email.api.dto.MailResponseDto;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
	
	@Value("${spring.mail.username}")
	private String sender;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private Configuration config;
	
	public MailResponseDto sendMail(MailRequestDto request, Map<String, Object> model,EmailType emailType) {
		MailResponseDto response = new MailResponseDto();
		MimeMessage message = mailSender.createMimeMessage();
		
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message,MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,StandardCharsets.UTF_8.name());
			Template template = null;
			
			switch (emailType) {
			case placeBooking:
				// booking placed mail
				template = config.getTemplate("booking-placed.ftl");
				break;
			case cancelBooking:
				// cancel booking mail
				template = config.getTemplate("booking-cancelled.ftl");
				break;
			}
			
			String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
			
			helper.setTo(request.getTo());
			helper.setText(html,true);
			helper.setSubject(request.getSubject());
			helper.setFrom(sender);
			
			mailSender.send(message);
			
			response.setMessage("E-mail successfully sent");
			response.setStatusCode(200);
			
		} catch (MessagingException | IOException | TemplateException e) {
			response.setMessage("E-mail sending failed :" + e.getMessage());
			response.setStatusCode(500);
		}
		return response;
	}
	
	public enum EmailType {placeBooking,cancelBooking};
}
