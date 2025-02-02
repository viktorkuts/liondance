package com.liondance.liondance_backend.logiclayer.Feedback;

import com.liondance.liondance_backend.datalayer.Event.Event;
import com.liondance.liondance_backend.datalayer.Event.EventRepository;
import com.liondance.liondance_backend.datalayer.Event.EventStatus;
import com.liondance.liondance_backend.datalayer.Event.EventType;
import com.liondance.liondance_backend.datalayer.Feedback.Feedback;
import com.liondance.liondance_backend.datalayer.Feedback.FeedbackRepository;
import com.liondance.liondance_backend.presentationlayer.Feedback.FeedbackResponseModel;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.Mockito.when;


class FeedbackServiceImplUnitTest {
    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getFeedbackByEventId_returnsFeedback_whenEventIdExists() {
        String eventId = "e5b07c6c6-dc48-489f-aa20-0d7d6fb12874";
        Feedback feedback = Feedback.builder()
                .feedbackId("e4e02950-20ca-4010-87cb-8c20e3d3354b")
                .timestamp(Instant.now())
                .feedback("Great event!")
                .rating(5)
                .eventId(eventId)
                .build();
        Event event = Event.builder()
                .eventId(eventId)
                .eventDateTime(Instant.now())
                .eventType(EventType.BIRTHDAY)
                .eventStatus(EventStatus.PENDING)
                .build();

        when(feedbackRepository.findAll()).thenReturn(Flux.just(feedback));
        when(eventRepository.findEventByEventId(eventId)).thenReturn(Mono.just(event));

        Flux<FeedbackResponseModel> result = feedbackService.getFeedbackByEventId(eventId);

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getFeedback().equals("Great event!") &&
                                response.getEvent().equals(event))
                .verifyComplete();
    }

    @Test
    void getFeedbackByEventId_throwsNotFoundException_whenEventNotFound() {
        String eventId = "e5b07c6c6-dc48-489f-aa20-0d7d6fb12874gf";
        Feedback feedback = Feedback.builder()
                .feedbackId("e4e02950-20ca-4010-87cb-8c20e3d3354b")
                .timestamp(Instant.now())
                .feedback("Great event!")
                .rating(5)
                .eventId(eventId)
                .build();

        when(feedbackRepository.findAll()).thenReturn(Flux.just(feedback));
        when(eventRepository.findEventByEventId(eventId)).thenReturn(Mono.empty());

        Flux<FeedbackResponseModel> result = feedbackService.getFeedbackByEventId(eventId);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("No feedback found for event ID: " + eventId))
                .verify();
    }
}