package com.breakbooking.eventbookingapi.model;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {

	@NotNull
	private String street;
	@NotNull
	private String city;
	@NotNull
	private String postCode;



}
