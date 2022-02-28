package com.breakbooking.eventbookingapi.common;

import com.breakbooking.eventbookingapi.common.enums.PaymentStatus;
import com.breakbooking.eventbookingapi.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {

    private Boolean success;
    private Integer code;
    private String message;
    private Payment payment;
    private LocalDateTime paidAt;

}
