package com.breakbooking.eventbookingapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.breakbooking.eventbookingapi.exception.ResourceNotFoundException;
import com.breakbooking.eventbookingapi.model.Event;
import com.breakbooking.eventbookingapi.model.Location;
import com.breakbooking.eventbookingapi.repository.EventRepository;


/* USING MOCKITO AND ASSERTJ */

/* JUnit 5 */

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
	
	@Mock
	private EventRepository eventRepository;
	
	private EventServiceImpl underTest;

	@BeforeEach
	void setUp() throws Exception {
		underTest = new EventServiceImpl(eventRepository);
	}

	@AfterEach
	void tearDown() throws Exception {
	}


	/* TEST FIND ALL EVENTS  */

	
	@Test
	public void itShouldFindAllEventsSuccessfully() {
	//Given
	
				
	//when
		
		underTest.findAllEvents();
		
	//then
		
		verify(eventRepository).findAll();
	}

	
	/* TEST FIND EVENT BY ID */
	
	
	@Test
	public void itShouldFindEventByIdSuccessfullyWhenIdExists() {

		Event event = new Event("testId","testEvent",LocalDateTime.of(2022, 01, 01, 9, 00,30),LocalDateTime.of(2022, 01, 01, 6, 00,30),
				new Location("street","city","postcode"),BigDecimal.valueOf(99.99),"Description about the event");
		
		given(eventRepository.findById(event.getEid())).willReturn(Optional.of(event));
		
		Event expected=underTest.findEventById(event.getEid());
		
		assertThat(expected).isEqualTo(event);
		
	}
	
	
	@Test
	public void findEventByIdWillThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
	
		String id="id";
		
		given(eventRepository.findById(id)).willReturn(Optional.empty());
		
		assertThatThrownBy(()->underTest.findEventById(id))
	   					.isInstanceOf(ResourceNotFoundException.class)
	   					.hasMessageContaining("Event by id "+ id +" was not found");
		
	}
	

	/* TEST ADD EVENT  */

	
	@Test
	public void itShouldAddEventSuccessfully() {

		/*Event event = new Event("testId","testEvent",LocalDateTime.of(2022, 01, 01, 9, 00,30,001),LocalDateTime.of(2022, 01, 01, 6, 00,300),
				new Location("street","city","postcode"),BigDecimal.valueOf(99.99),"Description about the event");
		*/
		Event event = new Event("testId","testEvent", LocalDateTime.parse("2022-01-01T09:00:00.100"),LocalDateTime.parse("2022-01-01T06:00:00.200"),
				new Location("street","city","postcode"), BigDecimal.valueOf(99.99),"Description about the event");


		underTest.addEvent(event);
		
		ArgumentCaptor<Event> eventArugumentCaptor= 
				ArgumentCaptor.forClass(Event.class);
		
		verify(eventRepository).save(eventArugumentCaptor.capture());
		
		Event capturedEvent = eventArugumentCaptor.getValue();
		
		assertThat(capturedEvent).isEqualTo(event);
		
		
	}
	
	
	/* TEST UPDATE EVENT */

	
	@Test
	public void itShoudlUpdateEventSuccessfullyWhenIdExists() {

		Event oldEvent = new Event("testId","testEvent",LocalDateTime.of(2022, 01, 01, 9, 00,30),LocalDateTime.of(2022, 01, 01, 6, 00,30),
				new Location("street","city","postcode"),BigDecimal.valueOf(99.99),"Description about the event");

		Event updateEvent = new Event("updatedEvent",LocalDateTime.of(2022, 02, 04, 9, 00,30),LocalDateTime.of(2022, 02, 04, 10, 00,00),
				new Location("updatedStreet","updatedCity","updatedPostcode"),BigDecimal.valueOf(49.99),"Updated description about the event");
		
		updateEvent.setEid(oldEvent.getEid());
				given(eventRepository.findById(oldEvent.getEid())).willReturn(Optional.of(oldEvent));
				
				underTest.updateEvent(oldEvent.getEid(),updateEvent);
				
				ArgumentCaptor<Event> eventArugumentCaptor= 
						ArgumentCaptor.forClass(Event.class);
				
				verify(eventRepository).save(eventArugumentCaptor.capture());
				
				Event capturedEvent = eventArugumentCaptor.getValue();
			
				assertThat(capturedEvent.toString()).isEqualTo(updateEvent.toString());
			
	}
	
	
	@Test
	public void updateEventWillThrowResourceNotFoundExceptionWhenIdDoesNotExist() {


		Event updateEvent = new Event("updatedEvent",LocalDateTime.of(2022, 02, 04, 9, 00,30),LocalDateTime.of(2022, 02, 04, 10, 00,00),
				new Location("street","city","postcode"),BigDecimal.valueOf(99.99),"Description about the event");
		
		String id="randomId";
				
				given(eventRepository.findById(id)).willReturn(Optional.empty());
				
				assertThatThrownBy(()->underTest.updateEvent(id,updateEvent))
						.isInstanceOf(ResourceNotFoundException.class)
						.hasMessageContaining("Event by id "+id+ " was not found");
				
				verify(eventRepository,never()).save(any());
			
	}
	
	
	/* TEST DELETE EVENT */
	
	
	@Test
	public void deleteEventSuccessfullyWhenIdExist() {

		Event event = new Event("testId","testEvent",LocalDateTime.of(2022, 01, 01, 9, 00,30),LocalDateTime.of(2022, 01, 01, 6, 00,30),
				new Location("street","city","postcode"),BigDecimal.valueOf(99.99),"Description about the event");



		given(eventRepository.existsById(event.getEid()))
		.willReturn(true);
	
		Map<String, Boolean> expected = underTest.deleteEvent(event.getEid());
	
		verify(eventRepository,times(1)).deleteById(event.getEid());
		
		assertThat(expected.get("Event deleted with success!")).isTrue();
	
	}
	
	
	@Test
	public void deleteEventWillThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

		String id="randomId";
	
		given(eventRepository.existsById(id))
		.willReturn(false);
	
		assertThatThrownBy(()-> underTest.deleteEvent(id))
							.isInstanceOf(ResourceNotFoundException.class)
							.hasMessage("Event by id "+id +" was not found");
			
		verify(eventRepository,never()).deleteById(any());
	
	}
	
	
	/* TEST DELETE ALL EVENTS */
	
	
	@Test
	public void deleteAllEventSuccessfully() {


		Map<String, Boolean> expected = underTest.deleteAllEvents();
	
		verify(eventRepository,times(1)).deleteAll();
		
		assertThat(expected.get("All Events deleted with success!")).isTrue();
	
	}
	
	
	/* TEST SEARCH FOR ALL EVENTS IN BETWEEN TWO DATES */
	

	@Test
	public void itShouldSearchAllEventsBetweenSuccessfully() {

		LocalDate start=LocalDate.of(2021, 12, 01);
		LocalDate end=LocalDate.of(2021, 12, 31);
		
		underTest.findAllEventsBetween(start, end);
		
		verify(eventRepository).findByTitleBetween(start, end);
		
		verify(eventRepository,times(1)).findByTitleBetween(start, end);
		
	}

	
	/* FIND ALL EVENTS IN PAGE -> PAGINATION */
	
	
	@Test
	public void itShouldGetAllEventsInpageuccessfully() {
	

		List<Event> events=new ArrayList<>();

		events.add(new Event("testId1","testEvent1",LocalDateTime.of(2022, 01, 01, 9, 00,30),LocalDateTime.of(2022, 01, 01, 6, 00,30),
				new Location("street","city","postcode"), BigDecimal.valueOf(99.99), "Description about the event"));

		events.add(new Event("testId2","testEvent2",LocalDateTime.of(2022, 02, 02, 10, 30,30),LocalDateTime.of(2022, 02, 02, 07, 30,30),
				new Location("street","city","postcode"), BigDecimal.valueOf(49.99),"Description about the event"
		));
		
		events.add(new Event("testId3","testEvent3",LocalDateTime.of(2022, 02, 02, 10, 30,30),LocalDateTime.of(2022, 02, 02, 07, 30,30),
				new Location("street","city","postcode"),BigDecimal.valueOf(49.99),"Description about the event"));
		
			
		Pageable pageRequest=PageRequest.of(0, 2,Sort.by("eid"));

		Page<Event> eventPage = new PageImpl<>(events);
		
		
		given(eventRepository.findAll(pageRequest)).willReturn(eventPage);

		
		underTest.findEventsWithPaginationAndSort(0, 2, "eid");
		
		verify(eventRepository).findAll(pageRequest);
		
		assertThat(eventPage.getTotalElements()).isEqualTo(3);
		assertThat(eventPage.getTotalPages()).isEqualTo(1);
		assertThat(eventPage.getNumber()).isEqualTo(0);
		assertThat(eventPage.getContent()).isEqualTo(events);
	

	}
	
}
