package com.breakbooking.eventbookingapi.repository;

import com.breakbooking.eventbookingapi.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BookingRepository extends MongoRepository<Booking,String> {

//    @Query(value="{'bookerEmail': ?0}", count=true)
//    Boolean selectExistsEmail(String email);

    List<Booking> findByEventEid(String eid);

    Booking findByBookerEmail(String bookerEmail);

    @Query(value = "{'userId': ?0 }",fields = "{'_id':0}")
    List<Booking> findByUserId(String id);

    @Query(value="{'userId': ?0}", count=true)
    Long findBookingByUserId(String id);


}
