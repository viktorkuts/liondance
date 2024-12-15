package com.liondance.liondance_backend.presentationlayer.Event;

import com.liondance.liondance_backend.datalayer.Event.Event;
import com.liondance.liondance_backend.datalayer.Event.EventStatus;
import com.liondance.liondance_backend.datalayer.Event.EventType;
import com.liondance.liondance_backend.datalayer.Event.PaymentMethod;
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

import java.time.LocalDateTime;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestModel {
    @NotBlank(message = "firstName is required")
    private String firstName;
    private String middleName;
    @NotBlank(message = "lastName is required")
    private String lastName;
    @Email(
            message = "email is invalid",
            regexp = "^(?![_.])[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            flags = Pattern.Flag.CASE_INSENSITIVE
    )
    @NotBlank(message = "email is required")
    private String email;
    @NotBlank(message = "phone is required")
    private String phone;
    @NotNull(message = "address is required")
    private Address address;
    @NotNull(message = "eventDateTime is required")
    private LocalDateTime eventDateTime;
    @NotNull(message = "eventType is required")
    private EventType eventType;
    @NotNull(message = "paymentMethod is required")
    private PaymentMethod paymentMethod;
    private String specialRequest;
    private EventStatus eventStatus;

    public static Event toEntity(EventRequestModel eventRequestModel) {
        Event newEvent = new Event();
        BeanUtils.copyProperties(eventRequestModel, newEvent);
        return newEvent;
    }
}
