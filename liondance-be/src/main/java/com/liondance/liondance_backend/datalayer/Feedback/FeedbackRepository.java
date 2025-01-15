package com.liondance.liondance_backend.datalayer.Feedback;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FeedbackRepository extends ReactiveMongoRepository<Feedback, String> {
}