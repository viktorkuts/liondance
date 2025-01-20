package com.liondance.liondance_backend.presentationlayer.Event;

import com.liondance.liondance_backend.datalayer.common.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDisplayDTO {
    private String eventId;
    private String eventDateTime;
    private String eventType;
    private String eventPrivacy;
    private Address eventAddress;
}