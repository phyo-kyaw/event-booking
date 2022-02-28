package com.breakbooking.eventbookingapi.model;

import com.breakbooking.eventbookingapi.common.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {


    private String paymentId;
    private String transactionId;
    private String bookingId;
    private BigDecimal price;
    private String nonce;
    private PaymentStatus paymentStatus;



}
