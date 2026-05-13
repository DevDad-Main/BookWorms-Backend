package com.devdad.book_worms.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler
	public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException exception) {

		Map<String, Object> errors = new HashMap<>();

		exception.getBindingResult()
				.getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

		return ResponseEntity.badRequest().body(errors);
	}

	@ExceptionHandler
	public ResponseEntity<Map<String, Object>> handleEmailAlreadyExistsException(EmailAlreadyExistsException exception) {
		log.warn("[ERR HANDLER]: " + "Email address already exists {}", exception.getMessage());

		Map<String, Object> errors = new HashMap<>();
		errors.put("message", "Email address already exists.");
		return ResponseEntity.badRequest().body(errors);
	}

	@ExceptionHandler
	public ResponseEntity<Map<String, Object>> handleTokenNotFoundException(TokenNotFoundException exception){
		log.warn("[ERR HANDLER]: " + "Token not found. {}", exception.getMessage());

		Map<String, Object> errors = new HashMap<>();
		errors.put("message", "Invalid Token, Token Not Found.");
		return ResponseEntity.badRequest().body(errors);
	}


	@ExceptionHandler
	public ResponseEntity<Map<String, Object>> handleExpiredActivationTokenException(ExpiredActivationTokenException exception){
		log.warn("[ERR HANDLER]: " + "Expired activation token. {}", exception.getMessage());

		Map<String, Object> errors = new HashMap<>();
		errors.put("message", "Expired Activation Token.");
		return ResponseEntity.badRequest().body(errors);
	}

}
