package com.liondance.liondance_backend.presentationlayer.Event;

import com.liondance.liondance_backend.datalayer.Event.*;
import com.liondance.liondance_backend.datalayer.common.Address;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class EventRequestModel {
    @NotNull(message = "address is required")
    private Address venue;
    @NotNull(message = "eventDateTime is required")
    private Instant eventDateTime;
    @NotNull(message = "eventType is required")
    private EventType eventType;
    @NotNull(message = "paymentMethod is required")
    private PaymentMethod paymentMethod;
    private String specialRequest;
    private EventStatus eventStatus;
    @NotNull(message = "eventPrivacy is required")
    private EventPrivacy eventPrivacy;

    public static Event toEntity(EventRequestModel eventRequestModel) {
        Event newEvent = new Event();
        BeanUtils.copyProperties(eventRequestModel, newEvent);
        return newEvent;
    }
}
