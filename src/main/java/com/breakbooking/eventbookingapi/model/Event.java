package com.breakbooking.eventbookingapi.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
@Document
@NoArgsConstructor
public class Event implements Serializable {

	@Id
	private String eid;
	
	@NotNull
	private String title;
	
	@NotNull
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime startTime;
	
	@NotNull
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime endTime;

	@NotNull
	private Location location;

	@NotNull
	private BigDecimal price;

	@NotNull
	private String description;

	@DBRef(lazy = true)
//	@JsonManagedReference
	@JsonBackReference
	private List<Booking> booking=new ArrayList<>();

	public Event(String title, LocalDateTime startTime, LocalDateTime endTime, Location location, BigDecimal price, String description) {
		this.title = title;
		this.startTime = startTime;
		this.endTime = endTime;
		this.location = location;
		this.price = price;
		this.description = description;
	
	}

	public Event(String eid, String title, LocalDateTime startTime, LocalDateTime endTime, Location location, BigDecimal price, String description) {
		this.eid = eid;
		this.title = title;
		this.startTime = startTime;
		this.endTime = endTime;
		this.location = location;
		this.price = price;
		this.description = description;
		
	}

	public void setBooking(Booking booking) {

		this.booking.add(booking);
	}

	public void cancelBooking(Booking booking) {
		this.booking.remove(booking);
	}
}
