package com.breakbooking.eventbookingapi.service;


import com.breakbooking.eventbookingapi.VO.ResponseTemplateVO;
import com.breakbooking.eventbookingapi.common.BookingRequest;
import com.breakbooking.eventbookingapi.common.BookingResponse;
import com.breakbooking.eventbookingapi.common.PaymentRequest;
import com.breakbooking.eventbookingapi.common.PaymentResponse;
import com.breakbooking.eventbookingapi.exception.DuplicateResourceException;
import com.breakbooking.eventbookingapi.exception.ResourceNotFoundException;
import com.breakbooking.eventbookingapi.model.Booking;
import com.breakbooking.eventbookingapi.model.Event;

import com.breakbooking.eventbookingapi.common.enums.PaymentStatus;
import com.breakbooking.eventbookingapi.model.Payment;
import com.breakbooking.eventbookingapi.model.enums.BookingStatus;
import com.breakbooking.eventbookingapi.repository.BookingRepository;
import com.breakbooking.eventbookingapi.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private BookingRepository bookingRepository;
    private EventRepository eventRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, EventRepository eventRepository) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
    }


    @Autowired
    private KafkaTemplate<String, BookingRequest> kafkaTemplateEmail;
    private static final String TOPIC1 = "email";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplateString;
    private static final String TOPIC2 = "string";



    /* BEAN FOR CREATING A NEW TOPIC */


    @Bean
    public NewTopic createTopic1() {
        return new NewTopic(TOPIC1, 1, (short) 1);
    }


    @Bean
    public NewTopic createTopic2() {
        return new NewTopic(TOPIC2, 1, (short) 1);
    }

    //Another way of creating kafka topics

    @Value("${kafka.request.topic}")
    private String requestTopic;
    @Autowired
    private ReplyingKafkaTemplate<String, PaymentRequest, Payment> replyingKafkaTemplate;


    /* FIND ALL BOOKING  */

    @Override
    public List<Booking> findAllBookings() {
        return bookingRepository.findAll();
    }


    /* FIND BOOKING BY ID  */


    @Override
    public Booking findBookingById(String id) {

        return bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking by id " + id + " not found"));
    }


    /* FIND ALL BOOKINGS OF A SPECIFIC USER */


    @Override
    public ResponseTemplateVO findBookingsWithUser(String id) {

        ResponseTemplateVO vo = new ResponseTemplateVO();
        List<Booking> bookingsWithUser = bookingRepository.findByUserId(id);
        List<Event> events = eventRepository.findEventsByBookingId(id);
        //  vo.setBooking(booking);
        vo.setBooking(bookingsWithUser);
        vo.setCode(200);
        vo.setMessage("Bookings of user with " + id);
        vo.setSuccess(Boolean.TRUE);
        vo.setTimestamp(LocalDateTime.now());
        return vo;

    }


    /* FIND ALL EVENT BOOKINGS OF AN EVENT */


    @Override
    public List<Booking> findBookingsOfEvent(String eid) {

        if (!eventRepository.existsById(eid)) {
            throw new ResourceNotFoundException("Event by id " + eid + " was not found");
        }
        return bookingRepository.findByEventEid(eid);
    }


    @Override
    @Transactional
    public BookingResponse addBooking(Booking newBooking) throws Exception {

        Event event = eventRepository.findById(newBooking.getEventEid()).orElseThrow(() -> new ResourceNotFoundException("Event by id " + newBooking.getEventEid() + " was not found"));

        List<Booking> bookings = bookingRepository.findByEventEid(event.getEid());
        System.out.println("count is " + bookingRepository.findBookingByUserId(newBooking.getUserId()));


        for (Booking booking : bookings) {
            System.out.println("Booking:" + booking);
            if (booking.getUserId().equals(newBooking.getUserId()) && booking.getPaymentStatus().equals(PaymentStatus.SUCCESS)) {
                throw new DuplicateResourceException("User with id " + newBooking.getUserId() + " already exists!");
            }
        }

        newBooking.setPaymentStatus(PaymentStatus.PENDING);
        newBooking.setBookingStatus(BookingStatus.PENDING);
        bookingRepository.save(newBooking);
        event.setBooking(newBooking);
        eventRepository.save(event);


        //Do call to payment service api and pass the booking id and price


        System.out.println("booking" + newBooking);
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setBooking(newBooking);

        Payment payment = new Payment();
        payment.setBookingId(newBooking.getId());
        payment.setPrice(event.getPrice());
        paymentRequest.setPayment(payment);

        ProducerRecord<String, PaymentRequest> record = new ProducerRecord<>(requestTopic, null, paymentRequest.getBooking().getId(), paymentRequest);
        System.out.println("record is " + record);

        RequestReplyFuture<String, PaymentRequest, Payment> future = replyingKafkaTemplate.sendAndReceive(record);
        System.out.println("future is " + future);

        ConsumerRecord<String, Payment> paymentConsumerRecord = future.get();

        System.out.println("response is " + paymentConsumerRecord);

//        return new ResponseEntity<>(response.value(), HttpStatus.OK);

        System.out.println("*******************************************");
        System.out.println("Consumed booking id is " + paymentConsumerRecord.value().getBookingId());
        System.out.println("Consumed message about payment status is " + paymentConsumerRecord.value().getPaymentStatus());
        System.out.println("*******************************************");

/*
        ListenableFuture<SendResult<String, Payment>> paymentFuture = kafkaTemplatePayment.send(PAYMENT, payment);

        paymentFuture.addCallback(new ListenableFutureCallback<SendResult<String, Payment>>() {
            @Override
            public void onSuccess(SendResult<String, Payment> result) {

                log.info("Payment processing..");

            }

            @Override
            public void onFailure(Throwable ex) {

                log.info("Error! Payment failure..");

            }
        });
*/

//        String string = this.restTemplate.getForObject("http://localhost:8082/api/v1/payment/token", String.class);
//        System.out.println("rest template reply"+string);


        BookingResponse bookingResponse = new BookingResponse();


        if (paymentConsumerRecord.value().getPaymentStatus().equals(PaymentStatus.SUCCESS)) {

            bookingResponse.setPaymentStatus(paymentConsumerRecord.value().getPaymentStatus());
            newBooking.setPaymentStatus(paymentConsumerRecord.value().getPaymentStatus());
            newBooking.setBookingStatus(BookingStatus.PAID);
            bookingResponse.setPaidAmount(event.getPrice());
            bookingRepository.save(newBooking);
            log.info("Payment successful sending an email notification of booking");
            bookingResponse.setMessage("You successfully created your booking!");
            sendEmailNotification(paymentConsumerRecord.value());

        } else {
            bookingResponse.setPaymentStatus(paymentConsumerRecord.value().getPaymentStatus());
            newBooking.setPaymentStatus(paymentConsumerRecord.value().getPaymentStatus());
            newBooking.setBookingStatus(BookingStatus.UNPAID);
            bookingResponse.setPaidAmount(BigDecimal.ZERO);
            bookingRepository.save(newBooking);
            bookingResponse.setMessage("“Sorry, we were not able to take your booking due to payment failure. Please try again");
            log.info("Payment failed. Don't send booking notification");
        }
        bookingResponse.setBooking(newBooking);
        bookingResponse.setSuccess(Boolean.TRUE);
        bookingResponse.setBookedAt(LocalDateTime.now());
        bookingResponse.setEvent(event);
        bookingResponse.setTransactionId(payment.getTransactionId());
        bookingResponse.setCode(201);
        return bookingResponse;
    }

/*

    @KafkaListener(topics="payment", groupId="payment-group",
            containerFactory = "paymentKafkaListenerFactory")
    public String consume(Payment payment){

        System.out.println("*******************************************");
        System.out.println("Consumed booking id is "+payment.getBookingId());
        System.out.println("Consumed message about payment status is "+payment.getPaymentStatus());
        System.out.println("*******************************************");
        Booking newBooking=bookingRepository.findById(payment.getBookingId()).orElseThrow(()->new ResourceNotFoundException("Booking by id "+payment.getBookingId()+" not found"));
        if(payment.getPaymentStatus().equals(PaymentStatus.SUCCESS)){
            log.info("Payment successful sending an email notification of booking");
            newBooking.setPaymentStatus(PaymentStatus.SUCCESS);
            bookingRepository.save(newBooking);
            sendEmailNotification(payment);
            return "success";
        }else{
            log.info("Payment failed. Don't send booking notification");
            newBooking.setPaymentStatus(PaymentStatus.FAILED);
            bookingRepository.save(newBooking);
            return "fail";
        }

    }
*/

    public void sendEmailNotification(Payment payment) {
        Booking newBooking = bookingRepository.findById(payment.getBookingId()).orElseThrow(() -> new ResourceNotFoundException("Booking by id " + payment.getBookingId() + " not found"));
        Event event = eventRepository.findById(newBooking.getEventEid()).orElseThrow(() -> new ResourceNotFoundException("Event by id " + newBooking.getEventEid() + " was not found"));

        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setBookingId(payment.getBookingId());
        bookingRequest.setUserId(newBooking.getUserId());
        bookingRequest.setBookerName(newBooking.getBookerName());
        bookingRequest.setBookerEmail(newBooking.getBookerEmail());
        bookingRequest.setBookerPhone(newBooking.getBookerPhone());
        bookingRequest.setEventTitle(event.getTitle());
        bookingRequest.setEventPrice(event.getPrice());
        bookingRequest.setTransactionId(payment.getTransactionId());

        if (newBooking.getId() != null) {


            log.info("Booking Request is " + bookingRequest);
            ListenableFuture<SendResult<String, BookingRequest>> future = kafkaTemplateEmail.send(TOPIC1, bookingRequest);
            future.addCallback(new ListenableFutureCallback<SendResult<String, BookingRequest>>() {

                @Override
                public void onSuccess(SendResult<String, BookingRequest> result) {
                    log.info("Sending mail");
                }

                @Override
                public void onFailure(Throwable ex) {
                    log.error("Error! Mail could not send ");
                }

            });

        }
    }


    @Override
    @Transactional
    public Map<String, Boolean> deleteBooking(String id) {

        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking by id " + id + " was not found"));

        Event event = eventRepository.findByEid(booking.getEventEid());

        event.cancelBooking(booking);

        System.out.println("Your booking has been successfully cancelled!");
        bookingRepository.deleteById(id);
        eventRepository.save(event);

        Map<String, Boolean> response = new HashMap<>();
        response.put("Booking deleted with success!", Boolean.TRUE);
        return response;

    }


    /* DELETE ALL BOOKINGS  */


    @Override
    public Map<String, Boolean> deleteAllBookings() {

        Map<String, Boolean> response = new HashMap<>();
        response.put("All Bookings deleted with success!", Boolean.TRUE);
        bookingRepository.deleteAll();

        return response;
    }


    /* KAFKA EXAMPLES */


    @Override
    public String getKafka(String name) {
        kafkaTemplateString.send(TOPIC2, name);

        return name;
    }

}
