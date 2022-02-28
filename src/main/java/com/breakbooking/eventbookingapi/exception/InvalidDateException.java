package com.breakbooking.eventbookingapi.exception;

public class InvalidDateException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public InvalidDateException(String messgae) {
		super(messgae);
	}

	public InvalidDateException(String message, Throwable cause) {
		super(message, cause);
		
	}
	
}
