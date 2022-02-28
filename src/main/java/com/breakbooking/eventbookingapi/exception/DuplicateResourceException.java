package com.breakbooking.eventbookingapi.exception;

public class DuplicateResourceException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public DuplicateResourceException(String messgae) {
		super(messgae);
	}

	public DuplicateResourceException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
