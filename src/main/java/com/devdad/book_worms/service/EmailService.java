package com.devdad.book_worms.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.devdad.book_worms.model.email.EmailTemplateName;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {


		private final JavaMailSender mailSender;
		private final SpringTemplateEngine templateEngine;


		public void sendEmail(
				String to,
				String username,
				EmailTemplateName emailTemplate,
				String confirmationUrl,
				String activationCode,
				String subject
				)
		{
			String templateName;

		}

}
