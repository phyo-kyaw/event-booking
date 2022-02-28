package com.breakbooking.eventbookingapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(String messgae) {
		super(messgae);
	}

	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
		
	}
	
	
}
