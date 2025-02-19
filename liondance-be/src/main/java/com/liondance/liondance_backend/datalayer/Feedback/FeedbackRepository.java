package com.liondance.liondance_backend.datalayer.Feedback;

import com.liondance.liondance_backend.datalayer.common.Visibility;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FeedbackRepository extends ReactiveMongoRepository<Feedback, String> {
    Flux<Feedback> getFeedbacksByVisibility(Visibility visibility);
    Mono<Feedback> getFeedbackByFeedbackId(String feedbackId);
}