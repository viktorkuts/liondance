package com.liondance.liondance_backend.presentationlayer.ClassFeedback;

import com.liondance.liondance_backend.datalayer.ClassFeedback.ClassFeedback;
import com.liondance.liondance_backend.datalayer.ClassFeedback.ClassFeedbackReport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

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

public static ClassFeedbackReportResponseModel from (ClassFeedbackReport classFeedbackReport){
    ClassFeedbackReportResponseModel reportResponseModel = new ClassFeedbackReportResponseModel();
    BeanUtils.copyProperties(classFeedbackReport, reportResponseModel);
    return reportResponseModel;
}

}
