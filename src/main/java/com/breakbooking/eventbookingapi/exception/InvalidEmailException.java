package com.breakbooking.eventbookingapi.exception;

public class InvalidEmailException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public InvalidEmailException(String messgae) {
		super(messgae);
	}

	public InvalidEmailException(String message, Throwable cause) {
		super(message, cause);
		
	}

}
