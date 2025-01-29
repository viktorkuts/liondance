package com.liondance.liondance_backend.presentationlayer.ClassFeedback;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassFeedbackReportResponseModel {
private String reportId;
private LocalDate classDate;
private Double averageScore;
private List<ClassFeedbackResponseModel> feedbackDetails;
}
