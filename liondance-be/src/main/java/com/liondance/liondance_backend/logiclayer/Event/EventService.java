package com.liondance.liondance_backend.logiclayer.Event;

import com.liondance.liondance_backend.presentationlayer.Event.EventRequestModel;
import com.liondance.liondance_backend.presentationlayer.Event.EventResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventService {
    public Flux<EventResponseModel> getAllEvents();
    public Mono<EventResponseModel> bookEvent(Mono< EventRequestModel> eventRequestModel);
}
