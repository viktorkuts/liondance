package com.liondance.liondance_backend.datalayer.Feedback;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "feedbacks")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    @Id
    private String id;
    private String feedbackId;
    private Instant timestamp;
    private String feedback;
    private int rating;
    private String eventId;
}