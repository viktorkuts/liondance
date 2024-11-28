package com.liondance.liondance_backend.logiclayer.Event;

import com.liondance.liondance_backend.presentationlayer.Event.EventResponseModel;
import reactor.core.publisher.Flux;

public interface EventService {
    public Flux<EventResponseModel> getAllEvents();
}
