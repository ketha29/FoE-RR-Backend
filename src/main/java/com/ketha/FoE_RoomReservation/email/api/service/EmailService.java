package com.ketha.FoE_RoomReservation.email.api.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.ketha.FoE_RoomReservation.email.api.dto.MailRequestDto;
import com.ketha.FoE_RoomReservation.email.api.dto.MailResponseDto;
import com.ketha.FoE_RoomReservation.model.User;
import com.ketha.FoE_RoomReservation.model.User.UserType;
import com.ketha.FoE_RoomReservation.repository.UserRepository;

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
	
	@Autowired
	private UserRepository userRepository;

	public MailResponseDto sendMail(MailRequestDto request, Map<String, Object> model, EmailType emailType) {
		MailResponseDto response = new MailResponseDto();
		MimeMessage message = mailSender.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			Template template = null;

			switch (emailType) {
			case placeBooking:
				// booking placed mail
				template = config.getTemplate("booking-placed.ftl");
				break;
			case userCancelBooking:
				// cancel booking mail
				template = config.getTemplate("booking-cancelled.ftl");
				break;
			case notifyAdmin:
				template = config.getTemplate("user-cancelled-booking.ftl");
				break;
			case notifyUser:
				template = config.getTemplate("Admin-cancelled-booking.ftl");
				break;
			default:
				break;
			}

			String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
			
			if(emailType==EmailType.notifyAdmin) {
				helper.setTo(getAdminMail());
			}else {
				helper.setTo(request.getTo());
			}
			
			helper.setText(html, true);
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

	public enum EmailType {
		placeBooking, userCancelBooking, notifyAdmin, notifyUser
	}

	public List<String> formatDateList(List<Date> availableDateList) {
		
		List<String> dates = new ArrayList<String>();

		for (Date date : availableDateList) {
			SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy, EEE");
			dates.add(sdf.format(date));
		}
		return dates;
	}

	public String formatTime(Time time) {
		SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
		return sdf.format(time);
	};
	
	private String[] getAdminMail() {
		
		List<User> admins = userRepository.findUserByUserType(UserType.admin);
		String[] adminMailList = admins.stream().map(admin->admin.getEmail()).toArray(String[]::new);
		
		return adminMailList;
	}
}
