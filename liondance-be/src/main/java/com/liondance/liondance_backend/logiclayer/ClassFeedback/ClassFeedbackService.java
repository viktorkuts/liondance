package com.liondance.liondance_backend.logiclayer.ClassFeedback;

import com.liondance.liondance_backend.datalayer.Course.Course;
import com.liondance.liondance_backend.presentationlayer.ClassFeedback.ClassFeedbackReportResponseModel;
import com.liondance.liondance_backend.presentationlayer.ClassFeedback.ClassFeedbackRequestModel;
import com.liondance.liondance_backend.presentationlayer.ClassFeedback.ClassFeedbackResponseModel;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface ClassFeedbackService {
public Mono<ClassFeedbackResponseModel> addClassFeedback (Mono<ClassFeedbackRequestModel> classFeedbackRequestModel);

}
