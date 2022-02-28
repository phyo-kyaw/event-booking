package com.breakbooking.eventbookingapi.exception;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ApiExceptionHandler {
	
	@ExceptionHandler(value= {ResourceNotFoundException.class})
	public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException e){
	
		//1. Create payload containing exception details
	
		ApiException apiException = new ApiException(e.getMessage(), HttpStatus.NOT_FOUND, ZonedDateTime.now(ZoneId.of("UTC")));
		
		//2. Return response entity
		
		return new ResponseEntity<Object> (apiException,HttpStatus.NOT_FOUND);
	}
	
	
	@ExceptionHandler(value= {DuplicateResourceException.class})
	public ResponseEntity<Object> handleDuplicateResourceException(DuplicateResourceException e){
		
		ApiException apiException=new ApiException(e.getMessage(), HttpStatus.CONFLICT, ZonedDateTime.now(ZoneId.of("UTC")));
		return new ResponseEntity<Object> (apiException,HttpStatus.CONFLICT);
	}
	
	
	@ExceptionHandler(value= {InvalidDateException.class})
	public ResponseEntity<Object> handleInvalidDateException(InvalidDateException e){

		ApiException apiException=new ApiException(e.getMessage(), HttpStatus.BAD_REQUEST, ZonedDateTime.now(ZoneId.of("UTC")));
		return new ResponseEntity<Object> (apiException,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value= {InvalidEmailException.class})
	public ResponseEntity<Object> handleInvalidEmailException(InvalidEmailException e){

		ApiException apiException=new ApiException(e.getMessage(), HttpStatus.BAD_REQUEST, ZonedDateTime.now(ZoneId.of("UTC")));
		return new ResponseEntity<Object> (apiException,HttpStatus.BAD_REQUEST);
	}
	
}
