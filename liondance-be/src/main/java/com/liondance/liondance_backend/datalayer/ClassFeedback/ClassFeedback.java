package com.liondance.liondance_backend.datalayer.ClassFeedback;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "classfeedbacks")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ClassFeedback {
    @Id
    private String id;
    private String feedbackId;
    private LocalDate classDate;
    private Double score;
    private String comment;
}
