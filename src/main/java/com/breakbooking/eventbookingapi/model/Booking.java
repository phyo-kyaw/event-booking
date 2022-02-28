package com.breakbooking.eventbookingapi.model;

import com.breakbooking.eventbookingapi.common.enums.PaymentStatus;
import com.breakbooking.eventbookingapi.model.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    @Id
    private String id;

    @NotNull(message = "User Id can not be empty!")
    private String userId;

    @NotNull(message = "User Name can not be empty!")
    @Size(min=3,max=50,message = "User Name must be between 3 to 100 characters")
    private String bookerName;
    @NotNull

    @Pattern(regexp ="^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$",message = "Invalid Email!")
    private String bookerEmail;

    @NotNull
    private String bookerPhone;

    private PaymentStatus paymentStatus;

    private BookingStatus bookingStatus;

   /* @DBRef(lazy = true)
    @JsonBackReference
    private List<Event> event=new ArrayList<>();
    */

/*    @DBRef(lazy = true)
    @JsonBackReference*/

    private String eventEid;

    public Booking(String userId, String bookerName, String bookerEmail, String bookerPhone) {
        this.userId = userId;
        this.bookerName = bookerName;
        this.bookerEmail = bookerEmail;
        this.bookerPhone = bookerPhone;
    }

    public Booking(String userId, String bookerName, String bookerEmail, String bookerPhone, String eventEid) {
        this.userId = userId;
        this.bookerName = bookerName;
        this.bookerEmail = bookerEmail;
        this.bookerPhone = bookerPhone;
        this.eventEid = eventEid;
    }

/*  public void setEvent(Event event) {
        this.event.add(event);
    }*/

}
