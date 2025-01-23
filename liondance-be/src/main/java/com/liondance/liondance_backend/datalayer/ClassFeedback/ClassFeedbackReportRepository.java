package com.liondance.liondance_backend.datalayer.ClassFeedback;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ClassFeedbackReportRepository extends ReactiveMongoRepository<ClassFeedbackReport, String> {

}
