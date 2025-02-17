package com.liondance.liondance_backend.logiclayer.Event;

import com.liondance.liondance_backend.datalayer.Event.EventStatus;
import com.liondance.liondance_backend.datalayer.User.Student;
import com.liondance.liondance_backend.datalayer.User.User;
import com.liondance.liondance_backend.presentationlayer.Event.EventDisplayDTO;
import com.liondance.liondance_backend.presentationlayer.Event.EventRequestModel;
import com.liondance.liondance_backend.presentationlayer.Event.EventResponseModel;
import com.liondance.liondance_backend.presentationlayer.Feedback.FeedbackRequestModel;
import com.liondance.liondance_backend.presentationlayer.Feedback.FeedbackResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

public interface EventService {
    public Flux<EventResponseModel> getAllEvents();
    public Mono<EventResponseModel> bookEvent(Mono< EventRequestModel> eventRequestModel, User user);
    public Mono<EventResponseModel> updateEventStatus(String eventId, Mono<EventStatus> eventStatus);
    public Mono<EventResponseModel> getEventById(String eventId);
    public Mono<EventResponseModel> rescheduleEvent(String eventId, Instant eventDateTime);
    public Flux<EventResponseModel> getEventsByClientId(String clientId);
    public Flux<EventResponseModel> getFilteredEvents();
    public Mono<EventResponseModel> updateEventDetails(String eventId, EventRequestModel eventRequestModel);
    public Mono<FeedbackResponseModel> submitFeedback(String eventId, Mono<FeedbackRequestModel> feedbackRequestModel, User user);
    Mono<Void> requestFeedback(String eventId);
    Mono<EventResponseModel> assignPerformers(String eventId, List<String> performers);
    Mono<EventResponseModel> removePerformers(String eventId, List<String> performers);
    Mono<List<String>> getPerformers(String eventId);
}
