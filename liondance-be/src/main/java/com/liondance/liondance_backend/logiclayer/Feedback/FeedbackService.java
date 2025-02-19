package com.liondance.liondance_backend.logiclayer.Feedback;

import com.liondance.liondance_backend.datalayer.common.Visibility;
import com.liondance.liondance_backend.presentationlayer.Feedback.FeedbackResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FeedbackService {
    Flux<FeedbackResponseModel> getFeedbackByEventId(String eventId);
    Flux<FeedbackResponseModel> getFeedbackByVisibility(Visibility visibility);
    Mono<FeedbackResponseModel> updateEventFeedbackVisibility(String feedbackId, Visibility visibility);
}