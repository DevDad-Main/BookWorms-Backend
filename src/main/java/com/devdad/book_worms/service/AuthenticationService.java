package com.devdad.book_worms.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.devdad.book_worms.dto.auth.RegistrationRequestDTO;
import com.devdad.book_worms.role.Role;
import com.devdad.book_worms.role.RoleRepository;
import com.devdad.book_worms.user.Token;
import com.devdad.book_worms.user.TokenRepository;
import com.devdad.book_worms.user.User;
import com.devdad.book_worms.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final TokenRepository tokenRepository;
	private final EmailService emailService;

	public void register(RegistrationRequestDTO registrationRequestDTO) {
		Role userRole = roleRepository.findByName("USER")
				// TODO -> add custom exception.
				.orElseThrow(() -> new IllegalStateException("ROLE USER was not initialized."));

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

	private void sendValidationEmail(User user) {
		var newToken = generateAndSaveActivationToken(user);
		// Send email.
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
