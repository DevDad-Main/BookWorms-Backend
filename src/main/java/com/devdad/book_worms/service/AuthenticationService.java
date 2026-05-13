package com.devdad.book_worms.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.devdad.book_worms.dto.auth.AuthenticationRequestDTO;
import com.devdad.book_worms.dto.auth.AuthenticationResponseDTO;
import com.devdad.book_worms.dto.auth.RegistrationRequestDTO;
import com.devdad.book_worms.exception.EmailAlreadyExistsException;
import com.devdad.book_worms.exception.ExpiredActivationTokenException;
import com.devdad.book_worms.exception.TokenNotFoundException;
import com.devdad.book_worms.role.Role;
import com.devdad.book_worms.role.RoleRepository;
import com.devdad.book_worms.security.JwtService;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

import com.devdad.book_worms.model.email.EmailTemplateName;
import com.devdad.book_worms.model.user.Token;
import com.devdad.book_worms.respository.TokenRepository;
import com.devdad.book_worms.model.user.User;
import com.devdad.book_worms.respository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final TokenRepository tokenRepository;
	private final EmailService emailService;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	@Value("${application.mailing.frontend.activation-url}")
	private String activationUrl;

	public void register(RegistrationRequestDTO registrationRequestDTO) throws MessagingException {
		Role userRole = roleRepository.findByName("USER")
				// TODO -> add custom exception.
				.orElseThrow(() -> new IllegalStateException("ROLE USER was not initialized."));

		if (!userRepository.findByEmail(registrationRequestDTO.getEmail()).isEmpty()) {
			throw new EmailAlreadyExistsException(
					"User with that email already exists. " + registrationRequestDTO.getEmail());
		}

		User user = User.builder()
				.firstName(registrationRequestDTO.getFirstName())
				.lastName(registrationRequestDTO.getLastName())
				.email(registrationRequestDTO.getEmail())
				.password(passwordEncoder.encode(registrationRequestDTO.getPassword()))
				.accountLocked(false)
				.enabled(false)
				.roles(List.of(userRole))
				.build();

		userRepository.save(user);
		sendValidationEmail(user);
	}

	public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationRequestDTO) {
		var auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						authenticationRequestDTO.getEmail(),
						authenticationRequestDTO.getPassword()));

		var claims = new HashMap<String, Object>();
		var user = ((User) auth.getPrincipal());

		claims.put("fullName", user.fullName());
		var jwtToken = jwtService.generateToken(claims, user);

		return AuthenticationResponseDTO
				.builder()
				.token(jwtToken)
				.build();
	}

	// @Transactional
	public void activateAccount(String token) throws MessagingException {
		Token savedToken = tokenRepository.findByToken(token)
				.orElseThrow(() -> new TokenNotFoundException("Invalid token."));

		if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
			sendValidationEmail(savedToken.getUser());
			throw new ExpiredActivationTokenException("Activation token has expired, A new token has been sent to your email.");
		}

		var user = userRepository.findById(savedToken.getUser().getId())
			.orElseThrow(() -> new UsernameNotFoundException("User not found."));

		user.setEnabled(true);
		userRepository.save(user);

		savedToken.setValidatedAt(LocalDateTime.now());
		tokenRepository.save(savedToken);

		sendWelcomeEmail(user);

	}

	private void sendValidationEmail(User user) throws MessagingException {
		String newToken = generateAndSaveActivationToken(user);
		// Send email.
		emailService.sendEmail(
				user.getEmail(), user.fullName(), EmailTemplateName.ACTIVATE_ACCOUNT, activationUrl, newToken,
				"Account Activation");
	}

	private void sendWelcomeEmail(User user) throws MessagingException {
		emailService.sendEmail(
				user.getEmail(), user.fullName(), EmailTemplateName.WELCOME, activationUrl, "",
				"Welcome to BookWorms");
	}

	private String generateAndSaveActivationToken(User user) {
		int codeLength = 6;
		String generatedToken = generateActivationCode(codeLength);
		Token token = Token.builder()
				.token(generatedToken)
				.createdAt(LocalDateTime.now())
				.expiresAt(LocalDateTime.now().plusMinutes(15))
				.user(user)
				.build();

		tokenRepository.save(token);
		return generatedToken;
	}

	private String generateActivationCode(int codeLength) {
		String characters = "0123456789";
		StringBuilder codeBuilder = new StringBuilder();

		// Ensures the random value is also cryptographically secure, better than
		// Random.
		SecureRandom secureRandom = new SecureRandom();

		for (int i = 0; i < codeLength; i++) {
			int randomindex = secureRandom.nextInt(characters.length());
			codeBuilder.append(characters.charAt(randomindex));
		}
		return codeBuilder.toString();
	}
}
