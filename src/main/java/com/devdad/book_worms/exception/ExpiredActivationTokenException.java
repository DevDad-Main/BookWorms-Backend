package com.devdad.book_worms.exception;

public class ExpiredActivationTokenException extends RuntimeException {

	public ExpiredActivationTokenException(String message) {
		super(message);
	}
}
