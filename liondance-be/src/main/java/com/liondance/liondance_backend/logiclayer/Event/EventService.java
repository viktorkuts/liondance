package com.liondance.liondance_backend.logiclayer.Event;

import com.liondance.liondance_backend.datalayer.Event.EventStatus;
import com.liondance.liondance_backend.presentationlayer.Event.EventDisplayDTO;
import com.liondance.liondance_backend.presentationlayer.Event.EventRequestModel;
import com.liondance.liondance_backend.presentationlayer.Event.EventResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

public interface EventService {
    public Flux<EventResponseModel> getAllEvents();
    public Mono<EventResponseModel> bookEvent(Mono< EventRequestModel> eventRequestModel);
    public Mono<EventResponseModel> updateEventStatus(String eventId, Mono<EventStatus> eventStatus);
    public Mono<EventResponseModel> getEventById(String eventId);
    public Mono<EventResponseModel> rescheduleEvent(String eventId, Instant eventDateTime);
    public Flux<EventResponseModel> getEventsByEmail(String email);
    public Flux<EventDisplayDTO> getFilteredEvents();
    public Mono<EventResponseModel> updateEventDetails(String eventId, EventRequestModel eventRequestModel);
}
