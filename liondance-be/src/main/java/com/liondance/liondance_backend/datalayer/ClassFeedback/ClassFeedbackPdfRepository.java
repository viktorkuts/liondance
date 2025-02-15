package com.liondance.liondance_backend.datalayer.ClassFeedback;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ClassFeedbackPdfRepository extends ReactiveMongoRepository<ClassFeedbackPdf, String> {
    Mono<ClassFeedbackPdf> findByReportId(String reportId);
}
