package com.liondance.liondance_backend.datalayer.ClassFeedback;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface ClassFeedbackRepository extends ReactiveMongoRepository<ClassFeedback, String> {
 Flux<ClassFeedback> findAllByClassDate(LocalDate classDate);
}
