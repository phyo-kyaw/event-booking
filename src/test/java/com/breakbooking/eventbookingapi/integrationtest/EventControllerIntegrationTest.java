package com.breakbooking.eventbookingapi.integrationtest;

import com.breakbooking.eventbookingapi.model.Event;
import com.breakbooking.eventbookingapi.model.Location;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EventControllerIntegrationTest {


    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    HttpHeaders headers = new HttpHeaders();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }


    /* TEST GET ALL EVENTS */


    @Test
    void testfindAllEventsSuccessfully() {

        ResponseEntity<String> events=testRestTemplate.getForEntity("http://localhost:"+port+"/api/v1/events/list",String.class );
        assertThat(events).isNotNull();
        assertThat(events.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
      //  assertThat(2).isEqualTo(events.getBody().size());
        assertThat(events.getStatusCode()).isEqualTo(HttpStatus.OK);

    }


    /* TEST GET EVENT BY ID */


    @Test
    void testfindEventByIdSuccessfullyWhenIdExists() {

        ResponseEntity<String> event=testRestTemplate.getForEntity("http://localhost:"+port+"/api/v1/events/find/testId",String.class );
        assertThat(event).isNotNull();
//        assertThat(event.getBody().getId()).isEqualTo(1001L);
//        assertThat(event.getBody().getName()).isEqualTo("Jack");
//        assertThat(event.getBody().getDob()).isEqualTo(LocalDate.of(2000, Month.APRIL, 06));
//        assertThat(event.getBody().getEmail()).isEqualTo("jack@gmail.com");
//        assertThat(event.getBody().getJobTitle()).isEqualTo("Java Developer");
//        assertThat(event.getBody().getDescription()).isEqualTo("Anything");
        assertThat(event.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(event.getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println(event);
    }

    @Test
    void testGetEventByIdWillThrowResourceNotFoundWhenIdDoesNotExist() {
        String id="fakeId";
        ResponseEntity<String> event=testRestTemplate.getForEntity("http://localhost:"+port+"/api/v1/events/find/"+id,String.class );
        assertThat(event.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(event.getBody()).contains("Event by id "+id+" was not found");
    }


    /* TEST ADD A NEW EVENT */


    @Test
    void testAddEventSuccessfullyWhenValidDataIsEntered() throws JSONException {
        System.out.println("Date is"+LocalDateTime.now());


       /* Event event = new Event("testId","testEvent", LocalDateTime.of(2022,01,01,9,00,00,100),LocalDateTime.of(2022,01,01,06,00,00,200),
                new Location("street","city","postcode"), BigDecimal.valueOf(99.99),"Description about the event");*/

        Event event = new Event("testId","testEvent", LocalDateTime.parse("2022-01-01T09:00:00.100"),LocalDateTime.parse("2022-01-01T06:00:00.200"),
                    new Location("street","city","postcode"), BigDecimal.valueOf(99.99),"Description about the event");


        HttpEntity<Event> request=new HttpEntity<Event>(event);
        ResponseEntity<String> responseEvent=testRestTemplate.postForEntity("http://localhost:"+port+"/api/v1/events/new",request,String
                .class );
        assertThat(responseEvent).isNotNull();
        assertThat(responseEvent.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
     //   assertThat(responseEvent.getBody().getName()).isEqualTo("Shwetali");
        assertThat(responseEvent.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        System.out.println(responseEvent);
//  Event expected1="{ eid: testId, title:testEvent, location: { street: street, city:city,postCode:postcode}, price:99.99,description:Description about the event}\n";
        String expected="{\n" +
                "    \"eid\": \"testId\",\n" +
                "    \"title\": \"testEvent\",\n" +
                "    \"startTime\": \"2022-01-01T09:00:00.1\",\n" +
                "    \"endTime\": \"2022-01-01T06:00:00.2\",\n" +
                "    \"location\": {\n" +
                "        \"street\": \"street\",\n" +
                "        \"city\": \"city\",\n" +
                "        \"postCode\": \"postcode\"\n" +
                "    },\n" +
                "    \"price\": 99.99,\n" +
                "    \"description\": \"Description about the event\"\n" +
                "}";
        JSONAssert.assertEquals(expected,responseEvent.getBody(),false);

    }


    /* TEST UPDATE EVENT */


    @Test
    void testUpdateEventSuccessfullyWhenValidDataIsEnteredAndIdExists() {

        Event oldEvent = new Event("613b2ce12128613b6954513e","testEvent",LocalDateTime.of(2022, 01, 01, 9, 00,30),LocalDateTime.of(2022, 01, 01, 6, 00,30),
                new Location("street","city","postcode"),BigDecimal.valueOf(99.99),"Description about the event");

        Event updateEvent = new Event("updatedEvent",LocalDateTime.of(2022, 02, 04, 9, 00,30),LocalDateTime.of(2022, 02, 04, 10, 00,00),
                new Location("updatedStreet","updatedCity","updatedPostcode"),BigDecimal.valueOf(49.99),"Upddated description about the event");


        HttpEntity<Event> request=new HttpEntity<Event>(updateEvent);
        ResponseEntity<String> response = testRestTemplate.exchange("http://localhost:"+port+"/api/v1/events/update/"+oldEvent.getEid(),HttpMethod.PUT,request,String.class);
        System.out.println("response is"+response);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }


    @Test
    void testUpdateCustomerWillThrowResourceNotFoundWhenIdDoesNotExist() {

        String id="fakeId";
        Event updateEvent = new Event("updatedEvent",LocalDateTime.of(2022, 02, 04, 9, 00,30),LocalDateTime.of(2022, 02, 04, 10, 00,00),
                new Location("updatedStreet","updatedCity","updatedPostcode"),BigDecimal.valueOf(49.99),"Upddated description about the event");


HttpEntity<Event> request=new HttpEntity<Event>(updateEvent);
ResponseEntity<String> response = testRestTemplate.exchange("http://localhost:"+port+"/api/v1/events/update/"+id,HttpMethod.PUT,request,String.class);
assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

}


/* TEST DELETE EVENT */


@Test
void testDeleteEventSuccessfullyWhenIdExists() {
ResponseEntity<String> response = testRestTemplate.exchange("http://localhost:"+port+"/api/v1/events/delete/testId",HttpMethod.DELETE,null,String.class);
assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
assertThat(response.getBody()).contains("Event deleted with success");
}

@Test
void testDeleteEventWillThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
String id="fakeId";
ResponseEntity<String> response = testRestTemplate.exchange("http://localhost:"+port+"/api/v1/events/delete/"+id,HttpMethod.DELETE,null,String.class);
assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
assertThat(response.getBody()).contains("Event by id "+id+" was not found");
}

@Test
void deleteAllEvents() {
}

@Test
void findEventsWithPaginationAndSort() {
}

@Test
void findEventsWithSorting() {
}

@Test
void findAllEventsBetween() {
}
}