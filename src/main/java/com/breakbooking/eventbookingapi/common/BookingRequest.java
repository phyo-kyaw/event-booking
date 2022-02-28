package com.breakbooking.eventbookingapi.common;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingRequest {

    private String bookingId;
    private String userId;
    private String bookerName;
    private String bookerEmail;
    private String bookerPhone;
    private String  eventTitle;
    private BigDecimal eventPrice;
    private String transactionId;

}
