package com.liondance.liondance_backend.logiclayer.ClassFeedback;

import com.liondance.liondance_backend.datalayer.ClassFeedback.ClassFeedback;
import com.liondance.liondance_backend.datalayer.ClassFeedback.ClassFeedbackPdf;
import com.liondance.liondance_backend.datalayer.ClassFeedback.ClassFeedbackReport;
import com.liondance.liondance_backend.datalayer.Course.Course;
import com.liondance.liondance_backend.presentationlayer.ClassFeedback.ClassFeedbackReportResponseModel;
import com.liondance.liondance_backend.presentationlayer.ClassFeedback.ClassFeedbackRequestModel;
import com.liondance.liondance_backend.presentationlayer.ClassFeedback.ClassFeedbackResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface ClassFeedbackService {
public Mono<ClassFeedbackResponseModel> addClassFeedback (Mono<ClassFeedbackRequestModel> classFeedbackRequestModel);
public Flux<ClassFeedbackReportResponseModel> getAllClassFeedbackReports();
public Mono<ClassFeedbackPdf> downloadClassFeedbackPdf(String reportId);
}
