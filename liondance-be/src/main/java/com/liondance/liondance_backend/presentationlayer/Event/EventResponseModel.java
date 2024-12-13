package com.liondance.liondance_backend.presentationlayer.Event;

import com.liondance.liondance_backend.datalayer.Event.Event;
import com.liondance.liondance_backend.datalayer.Event.EventStatus;
import com.liondance.liondance_backend.datalayer.Event.EventType;
import com.liondance.liondance_backend.datalayer.Event.PaymentMethod;
import com.liondance.liondance_backend.datalayer.common.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseModel {
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phone;
    private Address address;
    private LocalDateTime eventDateTime;
    private EventType eventType;
    private PaymentMethod paymentMethod;
    private String specialRequest;
    private EventStatus eventStatus;

    public static EventResponseModel from(Event event) {
        EventResponseModel responseModel = new EventResponseModel();
        BeanUtils.copyProperties(event, responseModel);
        return responseModel;
    }
}
