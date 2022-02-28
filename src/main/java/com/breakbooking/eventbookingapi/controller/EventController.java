package com.breakbooking.eventbookingapi.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.breakbooking.eventbookingapi.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.breakbooking.eventbookingapi.model.Event;

import io.swagger.annotations.ApiOperation;


@CrossOrigin(origins = {"http://localhost:4200", "http://localhost", "http://localhost:8082",
        "http://192.168.99.101", "http://ec2-3-104-154-174.ap-southeast-2.compute.amazonaws.com",
        "http://break-booking.online", "https://break-booking.online",
        "http://www.break-booking.online", "https://www.break-booking.online"})
@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }


    /* FIND ALL EVENTS */


    @GetMapping("/list")
    @ApiOperation(value = "Finds all Events")
    public ResponseEntity<List<Event>> findAllEvents() {

        List<Event> events = eventService.findAllEvents();
        return new ResponseEntity<>(events, HttpStatus.OK);

    }


    /* FIND AN EVENT BY ID */


    @GetMapping("/find/{id}")
    @ApiOperation(value = "Finds Events by id")
    public ResponseEntity<Event> findEventById(@PathVariable("id") String id) {
        return new ResponseEntity<>(eventService.findEventById(id), HttpStatus.OK);
    }


    /* CREATES A NEW EVENT */


    @PostMapping("/new")
    @ApiOperation(value = "Creates a new Event")
    public ResponseEntity<?> addEvent(@RequestBody Event event) {

        Event newEvent = eventService.addEvent(event);
        return new ResponseEntity<>(newEvent, HttpStatus.CREATED);

    }


    /* UPDATE AN EXISTING EVENT BY ID */


    @PutMapping("/update/{id}")
    @ApiOperation(value = "Updates an existing event by id")
    public ResponseEntity<Event> updateEvent(@PathVariable("id") String id, @RequestBody Event event) {

        Event updatedEvent = eventService.updateEvent(id, event);
        return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
    }


    /* DELETE AN EVENT BY ID */


    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "Deletes an event by id")
    public ResponseEntity<?> deleteEvent(@PathVariable("id") String id) {
        return new ResponseEntity<>(eventService.deleteEvent(id), HttpStatus.OK);

    }


    /* DELETE All EVENTS */


    @DeleteMapping("/deleteDb")
    @ApiOperation(value = "Deletes all events")
    public ResponseEntity<?> deleteAllEvents() {

        return new ResponseEntity<>(eventService.deleteAllEvents(), HttpStatus.OK);

    }


    /* FIND ALL EVENTS IN PAGE AND DYNAMIC SORTING  -> PAGINATION AND DYNAMIC SORTING */


    @GetMapping("/page")
    @ApiOperation(value = "Finds all events in page ")
    public Map<String, Object> findEventsWithPaginationAndSort(@RequestParam(name = "pageno", defaultValue = "0") int pageNo,
                                                               @RequestParam(name = "pagesize", defaultValue = "5") int pageSize,
                                                               @RequestParam(name = "sortby", defaultValue = "price") String sortBy) {

        return eventService.findEventsWithPaginationAndSort(pageNo, pageSize, sortBy);
    }


    /* DYNAMIC SORTING */


    @GetMapping("/sort/{field}")
    @ApiOperation(value = "Finds events in sorted order ")
    public ResponseEntity<List<Event>> findEventsWithSorting(@PathVariable("field") String field) {

        List<Event> events = eventService.findEventsWithSorting(field);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }


    /* FIND ALL EVENTS DURING A TIMEFRAME */


    @GetMapping("/date")
    @ApiOperation(value = "Finds all events duting a timeframe")
    public List<Event> findAllEventsBetween(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        return eventService.findAllEventsBetween(start, end);
    }


    /* GET ALL EVENTS WITH PRICE GREATER THAN  */
/*

    @GetMapping("/price")
    @ApiOperation(value = "Get all events with price greater than")
    public ResponseEntity<?> findAllEventsLessThanPrice(@RequestParam ("price") String price) {

        return new ResponseEntity<>(eventService.findAllEventsLessThanPrice(price), HttpStatus.OK);

    }*/


}
