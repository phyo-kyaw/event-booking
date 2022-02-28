package com.breakbooking.eventbookingapi.job;

import com.breakbooking.eventbookingapi.common.BookingRequest;
import com.breakbooking.eventbookingapi.common.enums.PaymentStatus;
import com.breakbooking.eventbookingapi.model.Booking;
import com.breakbooking.eventbookingapi.model.Event;
import com.breakbooking.eventbookingapi.model.enums.BookingStatus;
import com.breakbooking.eventbookingapi.repository.BookingRepository;
import com.breakbooking.eventbookingapi.repository.EventRepository;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.management.Query;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
public class job {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    private KafkaTemplate<String, BookingRequest> kafkaTemplateEmail;
    private static final String TOPIC = "reminderEmail";
    @Bean
    public NewTopic createTopic3() {
        return new NewTopic(TOPIC, 1, (short) 1);
    }

//    @Scheduled(fixedRate = 60000)

//       @Scheduled(cron="0 * * ? * *")// Every minute
    @Scheduled(cron = "0 */30 * ? * *") //	Every 30 minutes
    public void checkForUpcomingEvents() {
        // get all events scheduled in ~30 minutes
        System.out.println("job is on");

        List<Event> events = eventRepository.findByEventsBetween(LocalDateTime.now(), LocalDateTime.now().plusMinutes(30));
           System.out.println("events"+events);
        for (Event event : events) { // for each event
//            event.notifyUsers(); // notify all users concerned
            System.out.println("event is " + events);
            List<Booking> bookings = bookingRepository.findByEventEid(event.getEid());
            for (Booking booking : bookings) {
                BookingRequest bookingRequest = new BookingRequest();
                bookingRequest.setBookingId(booking.getId());
                bookingRequest.setUserId(booking.getUserId());
                bookingRequest.setBookerName(booking.getBookerName());
                bookingRequest.setBookerEmail(booking.getBookerEmail());
                bookingRequest.setBookerPhone(booking.getBookerPhone());
                bookingRequest.setEventTitle(event.getTitle());
                bookingRequest.setEventPrice(event.getPrice());
//            bookingRequest.setTransactionId(payment.getTransactionId());

                kafkaTemplateEmail.send(TOPIC, bookingRequest);
                System.out.println(booking + "You have an appointment withing 30 mins");
            }
        }


    }


    //Check for payment status for each 20 minutes

    @Scheduled(cron = "0 */20 * ? * *") //	Every 20 minutes
    public void checkForPaymentStatus() {
        List<Booking> bookings = bookingRepository.findAll();
        for (Booking booking : bookings) {
            if (booking.getPaymentStatus().equals(PaymentStatus.FAILED)) {
                System.out.println("Changed Payment Status from FAILED TO REJECTED");
                booking.setPaymentStatus(PaymentStatus.REJECTED);
                booking.setBookingStatus(BookingStatus.INACTIVE);
                bookingRepository.save(booking);
            }

        }
    }


}

