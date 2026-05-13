package com.devdad.book_worms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.devdad.book_worms.dto.auth.AuthenticationRequestDTO;
import com.devdad.book_worms.dto.auth.AuthenticationResponseDTO;
import com.devdad.book_worms.dto.auth.RegistrationRequestDTO;
import com.devdad.book_worms.service.AuthenticationService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

	private final AuthenticationService authenticationService;

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public ResponseEntity<?> register(
			@RequestBody @Valid RegistrationRequestDTO registrationRequest) throws MessagingException {
		authenticationService.register(registrationRequest);

		return ResponseEntity.accepted().build();
	}

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponseDTO> authenticate(
			@RequestBody @Valid AuthenticationRequestDTO authenticatedRequestDTO) {
		return ResponseEntity.ok(authenticationService.authenticate(authenticatedRequestDTO));
	}

	@GetMapping("/activate-account")
	public void confirm(
			@RequestParam String token) throws MessagingException {
		authenticationService.activateAccount(token);
	}

}
