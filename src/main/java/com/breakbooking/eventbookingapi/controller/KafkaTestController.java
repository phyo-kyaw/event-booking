package com.breakbooking.eventbookingapi.controller;


import com.breakbooking.eventbookingapi.common.BookingRequest;

import com.breakbooking.eventbookingapi.common.PaymentRequest;
import com.breakbooking.eventbookingapi.common.PaymentResponse;
import com.breakbooking.eventbookingapi.model.Booking;

import com.breakbooking.eventbookingapi.model.Payment;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("/api/v1/bookings")
public class KafkaTestController {
    @Value("${kafka.request.topic}")
    private String requestTopic;
    @Autowired
    private ReplyingKafkaTemplate<String, PaymentRequest, Payment> replyingKafkaTemplate;

    @PostMapping("/makePayment")
    public ResponseEntity<Payment> getObject(@RequestBody PaymentRequest paymentRequest)
            throws InterruptedException, ExecutionException, Exception {
        System.out.println("in kafka test controller");
        System.out.println("payment request is " + paymentRequest);
        ProducerRecord<String, PaymentRequest> record = new ProducerRecord<>(requestTopic, null, paymentRequest.getBooking().getId(), paymentRequest);
        System.out.println("record is " + record);
        RequestReplyFuture<String, PaymentRequest, Payment> future = replyingKafkaTemplate.sendAndReceive(record);
        System.out.println("future is " + future);

        ConsumerRecord<String, Payment> response = future.get();
        System.out.println("response is " + response);

        return new ResponseEntity<>(response.value(), HttpStatus.OK);
    }

    @Autowired
    private KafkaTemplate<String, BookingRequest> kafkaTemplateEmail;

    @PostMapping("/send-mail")
    public String getObject(@RequestBody BookingRequest bookingRequest)
            throws InterruptedException, ExecutionException, Exception {
        System.out.println("booking Request" + bookingRequest);
        kafkaTemplateEmail.send("Email", bookingRequest);
        return "done";
    }

}
