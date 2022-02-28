package com.breakbooking.eventbookingapi.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.breakbooking.eventbookingapi.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.breakbooking.eventbookingapi.model.Event;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends MongoRepository<Event, String>{
	
	@Query(value="{ 'startTime' : { $gte: ?0, $lte: ?1 } }", fields = "{'title':1,'startTime':1,'endTime':1,'_id':0}")
	List<Event> findByTitleBetween(LocalDate start, LocalDate end);

	Event findByEid(String eid);

	List<Event> findEventsByBookingId(String id);


//	@Query(value="{ 'startTime' : { {$gte: new Date().getTime()-(60*60*1000) } }", fields = "{'title':1,'startTime':1,'endTime':1,'_id':0}")
//    List<Event> findByStartTime(LocalDateTime startTime);

	@Query(value="{ 'startTime' : { $gte: ?0, $lte: ?1 } }")
	List<Event> findByEventsBetween(LocalDateTime start, LocalDateTime end);


//	db.event.find({$expr : {$lte : [{$toDouble:"$price"},99.99]}})
//	db.event.find({$expr : {$gte : [{$toDouble:"$price"},99]}}).pretty()
//	@Query(value = "{$expr : {$lte : [{$toDouble:'$price'}, 9]}}")
//	List<Event> findAllEventsLessThanPrice(BigDecimal amount);
}
