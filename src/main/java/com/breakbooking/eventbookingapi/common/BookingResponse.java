package com.breakbooking.eventbookingapi.common;

import com.breakbooking.eventbookingapi.common.enums.PaymentStatus;
import com.breakbooking.eventbookingapi.model.Booking;
import com.breakbooking.eventbookingapi.model.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {

    private Boolean success;
    private Integer code;
    private String message;
    private Booking booking;
    private Event event;
    private String transactionId;
    private PaymentStatus paymentStatus;
    private BigDecimal paidAmount;
    private LocalDateTime bookedAt;

}
