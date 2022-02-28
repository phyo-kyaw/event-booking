package com.breakbooking.eventbookingapi.service;

import com.breakbooking.eventbookingapi.model.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface EventService {

    List<Event> findAllEvents();

    Event findEventById(String id);

    Event addEvent(Event event);

    Event updateEvent(String id, Event updateEvent);

    Map<String, Boolean> deleteEvent(String id);

    Map<String,Boolean> deleteAllEvents();

    Map<String, Object> findEventsWithPaginationAndSort(int pageNo, int pageSize, String sortBy);

    List<Event> findAllEventsBetween(LocalDate start, LocalDate end);

    List<Event> findEventsWithSorting(String field);



//    List<Event> findAllEventsLessThanPrice(String price);
}
