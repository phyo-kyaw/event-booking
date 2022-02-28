package com.breakbooking.eventbookingapi.common;

import com.breakbooking.eventbookingapi.model.Booking;
import com.breakbooking.eventbookingapi.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

    private Booking booking;
    private Payment payment;

}
