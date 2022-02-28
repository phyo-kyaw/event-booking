package com.breakbooking.eventbookingapi.VO;

import com.breakbooking.eventbookingapi.model.Booking;
import com.breakbooking.eventbookingapi.model.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseVO {


    private Boolean success;
    private Integer code;
    private String message;
    private LocalDateTime timestamp;
    private List<Event> events;
//    private List<Booking> booking;
}
