package com.liondance.liondance_backend.logiclayer.Feedback;

import com.liondance.liondance_backend.datalayer.Event.EventRepository;
import com.liondance.liondance_backend.datalayer.Feedback.FeedbackRepository;
import com.liondance.liondance_backend.presentationlayer.Feedback.FeedbackResponseModel;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final EventRepository eventRepository;

    public FeedbackServiceImpl(FeedbackRepository feedbackRepository, EventRepository eventRepository) {
        this.feedbackRepository = feedbackRepository;
        this.eventRepository = eventRepository;
    }

    public Flux<FeedbackResponseModel> getFeedbackByEventId(String eventId) {
        return feedbackRepository.findAll()
                .filter(feedback -> feedback.getEventId().equals(eventId))
                .flatMap(feedback -> eventRepository.findById(feedback.getEventId())
                        .map(event -> FeedbackResponseModel.from(feedback).toBuilder().event(event).build()))
                .cast(FeedbackResponseModel.class)
                .switchIfEmpty(Mono.error(new NotFoundException("No feedback found for event ID: " + eventId)));
    }

}