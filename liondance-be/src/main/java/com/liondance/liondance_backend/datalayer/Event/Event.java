package com.liondance.liondance_backend.datalayer.Event;

import com.liondance.liondance_backend.datalayer.User.Student;
import com.liondance.liondance_backend.datalayer.common.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "events")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    private String id;
    private String eventId;
    private String clientId;
    private Address venue;
    private Instant eventDateTime;
    private EventType eventType;
    private PaymentMethod paymentMethod;
    private String specialRequest;
    private EventStatus eventStatus;
    private EventPrivacy eventPrivacy;
    @Builder.Default
    private Map<String, PerformerStatus> performers = new HashMap<>();
}
