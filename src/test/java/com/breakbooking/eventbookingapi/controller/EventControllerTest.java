package com.breakbooking.eventbookingapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.breakbooking.eventbookingapi.service.EventService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.breakbooking.eventbookingapi.exception.ResourceNotFoundException;
import com.breakbooking.eventbookingapi.model.Event;
import com.breakbooking.eventbookingapi.model.Location;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Disabled
@WebMvcTest(EventController.class)
class EventControllerTest {
	
private EventController underTest;
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private EventService eventService;
	
	@Autowired
	ObjectMapper mapper;

	@BeforeEach
	void setUp() throws Exception {
		
		mapper.registerModule(new JavaTimeModule());
	    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	    underTest = new EventController(eventService);


	}

	@AfterEach
	void tearDown() throws Exception {
	}


	/* TEST GET ALL EVENTS */

	
	@Test
	public void itShouldFindAllEventsSuccessfully() throws Exception {
		
		underTest.findAllEvents();
	
		verify(eventService).findAllEvents();
	
		verify(eventService,times(1)).findAllEvents();
		
		List<Event> events=new ArrayList<>();

		events.add(new Event("testId1","testEvent1",LocalDateTime.of(2022, 01, 01, 9, 00,30),LocalDateTime.of(2022, 01, 01, 6, 00,30),
				new Location("street","city","postcode"), BigDecimal.valueOf(99.99), "Description about the event"));
		
		events.add(new Event("testId2","testEvent2",LocalDateTime.of(2022, 02, 02, 10, 30,30),LocalDateTime.of(2022, 02, 02, 07, 30,30),
				new Location("street","city","postcode"), BigDecimal.valueOf(49.99),"Description about the event"
				));
		
		given(eventService.findAllEvents()).willReturn(events);
		
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/api/v1/events/list"))
								.andDo(print())
								.andExpect(jsonPath("$.[*]",hasSize(2)))
								.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
								.andExpect(status().isOk());
	
	}
	

	/*TEST FIND EVENT BY ID */
	
	
	@Test
	public void itShouldGetEventByIdSuccessfullyWhenIdExists() throws Exception {
		
		//mock the data by the event service class
		
		Event event = new Event("testId","testEvent",LocalDateTime.of(2022, 01, 01, 9, 00,30),LocalDateTime.of(2022, 01, 01, 6, 00,30),
				new Location("street","city","postcode"), BigDecimal.valueOf(99.99),"Description about the event");
		
		//Create a mock HTTP request to verify the expected result
		
		underTest.findEventById(event.getEid());
	
		verify(eventService).findEventById(event.getEid());
		
		given(eventService.findEventById(event.getEid())).willReturn(event);
		
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/api/v1/events/find/{id}",event.getEid()))
								.andDo(print())
								.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
								.andExpect(jsonPath("$.eid").value("testId"))
								.andExpect(jsonPath("$.title").value("testEvent"))
								.andExpect(jsonPath("$.startTime").value("2022-01-01T09:00:30"))
								.andExpect(jsonPath("$.endTime").value("2022-01-01T06:00:30"))
								.andExpect(jsonPath("$.location.street").value("street"))
								.andExpect(jsonPath("$.location.city").value("city"))
								.andExpect(jsonPath("$.location.postCode").value("postcode"))
								.andExpect(status().isOk());
	
	}
	
	
	@Test
	public void findEventByIdWillThrowResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception {
		
	underTest.findEventById(anyString());

	verify(eventService).findEventById(anyString());
	
	String id="randomid";
	
	given(eventService.findEventById(anyString())).willThrow(new ResourceNotFoundException("Event by id "+ id +" was not found"));

	mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/api/v1/events/find/{id}",id))
							.andDo(print())
							.andExpect((result)->  assertThat(result.getResolvedException() instanceof ResourceNotFoundException).isTrue())
							.andExpect((result)-> assertThat(result.getResolvedException().getMessage()).isEqualTo("Event by id "+id+" was not found") )
							.andExpect(status().isNotFound());
	}


	/* TEST ADD EVENT */
	

	@Test

	public void itShouldAddEventSuccessfully() throws Exception {
	
		Event event = new Event("testEvent",LocalDateTime.of(2022, 01, 01, 9, 00,30),LocalDateTime.of(2022, 01, 01, 6, 00,30),
				new Location("street","city","postcode"),BigDecimal.valueOf(99.99),"Description about the event");
		
		underTest.addEvent(event);
		
		verify(eventService).addEvent(event);
		
		given(eventService.addEvent(any(Event.class))).willReturn(event);
		
		mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/api/v1/events/new")
								.content(mapper.writeValueAsString(event))
								.contentType(MediaType.APPLICATION_JSON))
								.andDo(print())
							//	.andExpect(jsonPath("eid").value("testId"))
								.andExpect(jsonPath("$.title").value("testEvent"))
								.andExpect(jsonPath("$.startTime").value("2022-01-01T09:00:30"))
								.andExpect(jsonPath("$.endTime").value("2022-01-01T06:00:30"))
								.andExpect(jsonPath("$.location.street").value("street"))
								.andExpect(jsonPath("$.location.city").value("city"))
								.andExpect(jsonPath("$.location.postCode").value("postcode"))
								.andExpect(status().isCreated());
		
		
	}
	


	/* TEST UPDATE EVENT */
	
	
	@Test
	public void itShouldUpdateEventSuccessfullyWhenIdExist() throws  Exception {
		
		Event oldEvent = new Event("testId","testEvent",LocalDateTime.of(2022, 01, 01, 9, 00,30),LocalDateTime.of(2022, 01, 01, 6, 00,30),
				new Location("street","city","postcode"),BigDecimal.valueOf(99.99),"Description about the event");
		
		Event updateEvent = new Event("updatedEvent",LocalDateTime.of(2022, 02, 04, 9, 00,30),LocalDateTime.of(2022, 02, 04, 10, 00,00),
				new Location("updatedStreet","updatedCity","updatedPostcode"),BigDecimal.valueOf(49.99),"Updated description about the event");
		
		underTest.updateEvent("testid",updateEvent);
		
		verify(eventService).updateEvent("testid", updateEvent);
		
		given(eventService.updateEvent(anyString(),any(Event.class))).willReturn(updateEvent);
		
		mockMvc.perform(MockMvcRequestBuilders.put("http://localhost:8080/api/v1/events/update/{id}",oldEvent.getEid())
								.content(mapper.writeValueAsString(updateEvent))
								.contentType(MediaType.APPLICATION_JSON))
								.andDo(print())
							//	.andExpect(jsonPath("eid").value("testid"))
								.andExpect(jsonPath("$.title").value("updatedEvent"))
								.andExpect(jsonPath("$.startTime").value("2022-02-04T09:00:30"))
								.andExpect(jsonPath("$.endTime").value("2022-02-04T10:00:00"))
								.andExpect(jsonPath("$.location.street").value("updatedStreet"))
								.andExpect(jsonPath("$.location.city").value("updatedCity"))
								.andExpect(jsonPath("$.location.postCode").value("updatedPostcode"))
								.andExpect(status().isOk());
		
	}
	
	
	@Test
	public void updateEventWillThrowResourceNotForundExceptionWhenIdDoesNotExist() throws Exception{
		
		Event updateEvent = new Event("updatedEvent",LocalDateTime.of(2022, 02, 04, 9, 00,30),LocalDateTime.of(2022, 02, 04, 10, 00,00),
				new Location("street","city","postcode"),BigDecimal.valueOf(99.99),"Description about the event");
		
		String id="randomid";
		
		underTest.updateEvent(id,updateEvent);
		
		verify(eventService).updateEvent(id, updateEvent);
		
		given(eventService.updateEvent(anyString(),any(Event.class))).willThrow(new ResourceNotFoundException("Event by id "+id+ " was not found"));
		
		mockMvc.perform(MockMvcRequestBuilders.put("http://localhost:8080/api/v1/events/update/{id}",id)
								.content(mapper.writeValueAsString(updateEvent))
								.contentType(MediaType.APPLICATION_JSON))
								.andDo(print())
								.andExpect((result)->assertThat(result.getResolvedException() instanceof ResourceNotFoundException ).isTrue())
								.andExpect((result)->assertThat("Event by id "+id+ " was not found").isEqualTo(result.getResolvedException().getMessage()))
								.andExpect(status().isNotFound());
	}

	

	/* TEST DELETE EVENT */
	
	
	@Test
	public void itShouldDeleteEventSuccessfullyWhenIdExist() throws Exception {
	
		Event event = new Event("testId","testEvent",LocalDateTime.of(2022, 01, 01, 9, 00,30),LocalDateTime.of(2022, 01, 01, 6, 00,30),
				new Location("street","city","postcode"),BigDecimal.valueOf(99.99),"Description about the event");
		
		underTest.deleteEvent(event.getEid());
		
		verify(eventService).deleteEvent(event.getEid());
		
		Map<String,Boolean> response=new HashMap<>();
		response.put("Event delted with Success", Boolean.TRUE);
		
		given(eventService.deleteEvent(event.getEid())).willReturn(response);
		
		mockMvc.perform(MockMvcRequestBuilders.delete("http://localhost:8080/api/v1/events/delete/{id}",event.getEid())
								.contentType(MediaType.APPLICATION_JSON))
								.andDo(print())
								.andExpect(status().isOk());
		
	}
	
	
	@Test
	public void deleteEventWillThrowResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception {
		
	underTest.deleteEvent(anyString());

	verify(eventService).deleteEvent(anyString());
	
	String id="randomid";
	
	given(eventService.deleteEvent(anyString())).willThrow(new ResourceNotFoundException("Event by id "+id+ " was not found"));
	
	mockMvc.perform(MockMvcRequestBuilders.delete("http://localhost:8080/api/v1/events/delete/{id}",id))
							.andDo(print())
							.andExpect((result)->  assertThat(result.getResolvedException() instanceof ResourceNotFoundException).isTrue())
							.andExpect(status().isNotFound());
	
	}
	
	
	/* TEST DELETE ALL EVENTS */

	
	@Test
	public void itShouldDeleteAllEventsSuccessfully() throws Exception {
		
		underTest.deleteAllEvents();
	
		verify(eventService).deleteAllEvents();
	
		verify(eventService,times(1)).deleteAllEvents();
		
		Map<String,Boolean> response=new HashMap<>();
		response.put("All Events Deleted with success", Boolean.TRUE);
		
		given(eventService.deleteAllEvents()).willReturn(response);
		
		mockMvc.perform(MockMvcRequestBuilders.delete("http://localhost:8080/api/v1/events/deleteDb"))
								.andDo(print())
								.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
								.andExpect(status().isOk());
	
	}
	
	
	@Test
	public void itShouldGetAllEventsInPageSuccessfully() throws Exception {
		

		List<Event> events=new ArrayList<>();
		
		events.add(new Event("testId1","testEvent1",LocalDateTime.of(2022, 12, 01, 9, 00,30),LocalDateTime.of(2022, 12, 01, 6, 00,30),
				new Location("street","city","postcode"),BigDecimal.valueOf(99.99),"Description about the event"));
		
		events.add(new Event("testId2","testEvent2",LocalDateTime.of(2022, 12, 02, 10, 30,30),LocalDateTime.of(2022, 12, 02, 07, 30,30),
				new Location("street","city","postcode"),BigDecimal.valueOf(49.99),"Description about the event"));
				
		underTest.findEventsWithPaginationAndSort(0,2,"eid");
	
		verify(eventService).findEventsWithPaginationAndSort(0, 2, "eid");
		
		Map<String,Object> response=new HashMap<>();
	
		response.put("data", events);
		response.put("Total No. of Page", 1);
		response.put("Total no of Elements", 1);
		response.put("Current Page No.", 0);
		
		given(eventService.findEventsWithPaginationAndSort(0, 2, "eid")).willReturn(response);
	
		
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/api/v1/events/page")
								.param("pageno", "0")
								.param("pagesize", "2")
								.param("sortby", "eid"))
								.andDo(print())
								.andExpect(status().isOk());
	
	}

	/* TEST FIND ALL EVENTS DURING A TIMEFRAME */
	
	
	@Test
	public void itShouldSearchAllEventsBetweenSuccessfully() throws Exception {
		
		LocalDate start=LocalDate.of(2021, 12, 01);
		LocalDate end=LocalDate.of(2021, 12, 31);
		
	
		List<Event> events=new ArrayList<>();
		
		events.add(new Event("testId1","testEvent1",LocalDateTime.of(2022, 12, 01, 9, 00,30),LocalDateTime.of(2022, 12, 01, 6, 00,30),
				new Location("street","city","postcode"),BigDecimal.valueOf(99.99),"Description about the event"));
		
		events.add(new Event("testId2","testEvent2",LocalDateTime.of(2022, 12, 02, 10, 30,30),LocalDateTime.of(2022, 12, 02, 07, 30,30),
				new Location("street","city","postcode"),BigDecimal.valueOf(49.99),"Description about the event"));
		
		underTest.findAllEventsBetween(start,end);
	
		verify(eventService).findAllEventsBetween(start,end);
	
		verify(eventService,times(1)).findAllEventsBetween(start, end);
	
		
		given(eventService.findAllEventsBetween(start, end)).willReturn(events);
		
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/api/v1/events/date")
							.param("start", start.toString())
							.param("end", end.toString()))
							.andDo(print())
							.andExpect(jsonPath("$.[*]", hasSize(2)))
							.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
							.andExpect(status().isOk());
	
	}


	
	
}
