package com.liondance.liondance_backend.logiclayer.Feedback;

import com.liondance.liondance_backend.datalayer.Event.EventRepository;
import com.liondance.liondance_backend.datalayer.Feedback.FeedbackRepository;
import com.liondance.liondance_backend.datalayer.common.Visibility;
import com.liondance.liondance_backend.presentationlayer.Feedback.FeedbackResponseModel;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
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
                .flatMap(feedback -> eventRepository.findEventByEventId(feedback.getEventId())
                        .map(event -> FeedbackResponseModel.from(feedback).toBuilder().event(event).build()))
                .cast(FeedbackResponseModel.class)
                .switchIfEmpty(Mono.error(new NotFoundException("No feedback found for event ID: " + eventId)));
    }

    @Override
    public Flux<FeedbackResponseModel> getFeedbackByVisibility(Visibility visibility) {
        return feedbackRepository.getFeedbacksByVisibility(visibility)
                .map(FeedbackResponseModel::from);
    }

    @Override
    public Mono<FeedbackResponseModel> updateEventFeedbackVisibility(String feedbackId, Visibility visibility) {
        return feedbackRepository.getFeedbackByFeedbackId(feedbackId)
                .switchIfEmpty(Mono.error(new NotFoundException("No feedback found for event ID: " + feedbackId)))
                .map(e -> {
                    e.setVisibility(visibility);
                    return e;
                })
                .flatMap(feedbackRepository::save)
                .map(FeedbackResponseModel::from);
    }
}