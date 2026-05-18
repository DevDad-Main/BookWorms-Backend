package com.devdad.book_worms.exception;

public class OperationNotPermittedException extends RuntimeException {

	public OperationNotPermittedException(String message) {
		super(message);
	}
}
