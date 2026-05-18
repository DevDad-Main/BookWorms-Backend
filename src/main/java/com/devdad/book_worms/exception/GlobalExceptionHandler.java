package com.devdad.book_worms.exception;

import static com.devdad.book_worms.model.enums.BusinessErrorCodes.ACCOUNT_DISABLED;
import static com.devdad.book_worms.model.enums.BusinessErrorCodes.ACCOUNT_LOCKED;
import static com.devdad.book_worms.model.enums.BusinessErrorCodes.BAD_CREDENTIALS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.mail.MessagingException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	// Thrown if an authentication request is rejected because the account is
	// locked. Makes no assertion as to whether or not the credentials were valid.
	@ExceptionHandler(LockedException.class)
	public ResponseEntity<ExceptionResponse> handleException(LockedException exception) {
		return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body(ExceptionResponse.builder()
						.businessErrorCode(ACCOUNT_LOCKED.getCode())
						.businessErrorDescription(ACCOUNT_LOCKED.getDescription())
						.error(exception.getMessage())
						.build());
	}

	// Thrown if an authentication request is rejected because the account is
	// disabled. Makes no assertion as to whether or not the credentials were valid.
	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<ExceptionResponse> handleException(DisabledException exception) {
		return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body(ExceptionResponse.builder()
						.businessErrorCode(ACCOUNT_DISABLED.getCode())
						.businessErrorDescription(ACCOUNT_DISABLED.getDescription())
						.error(exception.getMessage())
						.build());
	}

	// Thrown if an authentication request is rejected because the credentials are
	// invalid. For this exception to be thrown, it means the account is neither
	// locked nor disabled.
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exception) {
		return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body(ExceptionResponse.builder()
						.businessErrorCode(BAD_CREDENTIALS.getCode())
						.businessErrorDescription(BAD_CREDENTIALS.getDescription())
						.error(exception.getMessage())
						.build());
	}

	// Thrown when we have an issue with the mailing system.
	@ExceptionHandler(MessagingException.class)
	public ResponseEntity<ExceptionResponse> handleException(MessagingException exception) {
		return ResponseEntity
				.status(INTERNAL_SERVER_ERROR)
				.body(ExceptionResponse.builder()
						.error(exception.getMessage())
						.build());
	}


	// Thrown when we a user tries to edit details that they are not permitted to do.
	@ExceptionHandler(OperationNotPermittedException.class)
	public ResponseEntity<ExceptionResponse> handleException(OperationNotPermittedException exception) {
		return ResponseEntity
				.status(BAD_REQUEST)
				.body(ExceptionResponse.builder()
						.error(exception.getMessage())
						.build());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exception) {
		Set<String> errors = new HashSet<>();

		exception.getBindingResult().getAllErrors()
				.forEach(error -> {
					var errorMessage = error.getDefaultMessage();
					errors.add(errorMessage);
				});

		return ResponseEntity
				.status(BAD_REQUEST)
				.body(ExceptionResponse.builder()
						.validationErrors(errors)
						.build());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> handleException(Exception exception) {

		exception.printStackTrace();

		return ResponseEntity
				.status(INTERNAL_SERVER_ERROR)
				.body(ExceptionResponse.builder()
						.businessErrorDescription("Internal Error, please contact the server admin.")
						.error(exception.getMessage())
						.build());
	}

	// @ExceptionHandler
	// public ResponseEntity<Map<String, Object>>
	// handleValidationExceptions(MethodArgumentNotValidException exception) {
	//
	// Map<String, Object> errors = new HashMap<>();
	//
	// exception.getBindingResult()
	// .getFieldErrors()
	// .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
	//
	// return ResponseEntity.badRequest().body(errors);
	// }

	@ExceptionHandler
	public ResponseEntity<Map<String, Object>> handleEmailAlreadyExistsException(EmailAlreadyExistsException exception) {
		log.warn("[ERR HANDLER]: " + "Email address already exists {}", exception.getMessage());

		Map<String, Object> errors = new HashMap<>();
		errors.put("message", "Email address already exists.");
		return ResponseEntity.badRequest().body(errors);
	}

	@ExceptionHandler
	public ResponseEntity<Map<String, Object>> handleTokenNotFoundException(TokenNotFoundException exception) {
		log.warn("[ERR HANDLER]: " + "Token not found. {}", exception.getMessage());

		Map<String, Object> errors = new HashMap<>();
		errors.put("message", "Invalid Token, Token Not Found.");
		return ResponseEntity.badRequest().body(errors);
	}

	@ExceptionHandler
	public ResponseEntity<Map<String, Object>> handleExpiredActivationTokenException(
			ExpiredActivationTokenException exception) {
		log.warn("[ERR HANDLER]: " + "Expired activation token. {}", exception.getMessage());

		Map<String, Object> errors = new HashMap<>();
		errors.put("message", "Expired Activation Token.");
		return ResponseEntity.badRequest().body(errors);
	}

}
