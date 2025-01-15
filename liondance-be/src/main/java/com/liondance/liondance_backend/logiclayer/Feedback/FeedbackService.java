package com.liondance.liondance_backend.logiclayer.Feedback;

import com.liondance.liondance_backend.presentationlayer.Feedback.FeedbackResponseModel;
import reactor.core.publisher.Flux;

public interface FeedbackService {
    Flux<FeedbackResponseModel> getFeedbackByEventId(String eventId);
}