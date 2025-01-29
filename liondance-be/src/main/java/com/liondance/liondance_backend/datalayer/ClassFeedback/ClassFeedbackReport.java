package com.liondance.liondance_backend.datalayer.ClassFeedback;

import com.liondance.liondance_backend.presentationlayer.ClassFeedback.ClassFeedbackResponseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "classreportfeedbacks")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ClassFeedbackReport {
    @Id
    private String id;
    private String reportId;
    private LocalDate classDate;
    private Double averageScore;
    private List<ClassFeedbackResponseModel> feedbackDetails;
}
