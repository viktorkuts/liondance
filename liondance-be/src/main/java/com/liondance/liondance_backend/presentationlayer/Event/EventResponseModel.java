package com.liondance.liondance_backend.presentationlayer.Event;

import com.liondance.liondance_backend.datalayer.Event.*;
import com.liondance.liondance_backend.datalayer.common.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseModel {
    private String eventId;
    private Address venue;
    private Instant eventDateTime;
    private EventType eventType;
    private PaymentMethod paymentMethod;
    private String specialRequest;
    private EventStatus eventStatus;
    private EventPrivacy eventPrivacy;

    public static EventResponseModel from(Event event) {
        EventResponseModel responseModel = new EventResponseModel();
        BeanUtils.copyProperties(event, responseModel);
        return responseModel;
    }
}
