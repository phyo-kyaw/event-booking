package com.breakbooking.eventbookingapi.service;

import com.breakbooking.eventbookingapi.exception.ResourceNotFoundException;
import com.breakbooking.eventbookingapi.model.Event;
import com.breakbooking.eventbookingapi.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Service
public class EventServiceImpl implements EventService {

    private EventRepository eventRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }


    /* FIND ALL EVENTS */


    @Override
    public List<Event> findAllEvents() {
        return eventRepository.findAll();
    }


    /* FIND AN EVENT BY ID */


    @Override
    public Event findEventById(String id) {
        return eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event by id " + id + " was not found"));
    }


    /* CREATE A NEW EVENT  */


    @Override
    public Event addEvent(Event event) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        event.setStartTime(LocalDateTime.parse(event.getStartTime().toString(), formatter));
        event.setEndTime(LocalDateTime.parse(event.getEndTime().toString(), formatter));

        return eventRepository.save(event);

    }


    /* UPDATE AN EXISTING EVENT  */


    @Override
    public Event updateEvent(String id, Event updateEvent) {
        Event oldEvent = eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event by id " + id + " was not found"));

        oldEvent.setTitle(updateEvent.getTitle());
        oldEvent.setStartTime(updateEvent.getStartTime());
        oldEvent.setEndTime(updateEvent.getEndTime());
        oldEvent.setLocation(updateEvent.getLocation());
        oldEvent.setPrice(updateEvent.getPrice());
        oldEvent.setDescription(updateEvent.getDescription());

        Event updatedEvent = eventRepository.save(oldEvent);

        return updatedEvent;

    }


    /* DELETE AN EVENT  */


    @Override
    public Map<String, Boolean> deleteEvent(String id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event by id " + id + " was not found");
        }

        eventRepository.deleteById(id);

        Map<String, Boolean> response = new HashMap<>();
        response.put("Event deleted with success!", Boolean.TRUE);
        return response;
    }



    /* DELETE ALL EVENT  */


    @Override
    public Map<String, Boolean> deleteAllEvents() {
        Map<String, Boolean> response = new HashMap<>();
        response.put("All Events deleted with success!", Boolean.TRUE);
        eventRepository.deleteAll();
        return response;
    }


    /* FIND EVENTS IN PAGE AND DYNAMIC SORTING  -> PAGINATION */


    @Override
    public Map<String, Object> findEventsWithPaginationAndSort(int pageNo, int pageSize, String sortBy) {

        Map<String, Object> response = new HashMap<>();

        Sort sort = Sort.by(sortBy);

        Pageable page = PageRequest.of(pageNo, pageSize, sort);

        Page<Event> eventPage = eventRepository.findAll(page);

        response.put("data", eventPage.getContent());
        response.put("Total No. of Page", eventPage.getTotalPages());
        response.put("Total no of Elements", eventPage.getTotalElements());
        response.put("Current Page No.", eventPage.getNumber());

        return response;
    }


    /* FIND ALL EVENTS DURING A TIMEFRAME */


    @Override
    public List<Event> findAllEventsBetween(LocalDate start, LocalDate end) {
        return eventRepository.findByTitleBetween(start, end);
    }


    /* DYNAMIC SORTING */


    @Override
    public List<Event> findEventsWithSorting(String field) {
        return eventRepository.findAll(Sort.by(Sort.Direction.ASC, field));
    }

/*   @Autowired
//    MongoTemplate mongoTemplate;

    @Override
    public List<Event> findAllEventsLessThanPrice(String price) {
//        Query query=new Query().addCriteria(Criteria.where("$price").lt(price));
//        List<Event> events=mongoTemplate.find(query,Event.class);

//        BigDecimal amount=new BigDecimal(price);
//        List<Event> events=eventRepository.findAllEventsLessThanPrice(amount);
//        return events;
    }*/


}



    /*YET TO BE DONE */

/*
	public Event getEventWithBooking(String bookingId) {

		EventBooking eventBooking=eventBookingRepository.findById(bookingId).orElseThrow(()->new ResourceNotFoundException("Event Booking Not Found"));


		for(EventBooking eventBooking:list) {
			if(eventBooking.getId().equals(bookingId))
				list.remove(eventBooking);
		}
		eventRepository.save(event);
		return event;


		EventBooking eventBooking=eventRepository.findEventbookingByEventbookingId(bookingId);

		return null;
	}
*/

    /* Adding Bookings */

//	public Map<String, Boolean> enrolledUsers(String eventId, String bookingId) {
//		System.out.println(eventId);
//		System.out.println(bookingId);
//		Event event=eventRepository.findById(eventId).orElseThrow(()->new ResourceNotFoundException("Event Not Found "));
//		Booking booking= bookingRepository.findById(bookingId).orElseThrow(()->new ResourceNotFoundException("Event Booking Not Found "));
//
//		System.out.println(event);
//		System.out.println(booking);
//		event.setEventbooking(booking);
//
//
//		booking.setEvent(event);
//		eventRepository.save(event);
//		bookingRepository.save(booking);
//
//		Map<String, Boolean> response=new HashMap<>();
//		response.put("Success", Boolean.TRUE);
//
//		return response;
//	}

//	public List<Event> findBookedEvents(String bookingId) {
//		return eventRepository.findByBookingId(bookingId);
//	}
//
//    public Map<String, Boolean> enrolledUsers(String eventId, String bookingId) {
//
//		return null;
//    }
//}
