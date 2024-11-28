package com.liondance.liondance_backend.logiclayer.Event;

import com.liondance.liondance_backend.datalayer.Event.EventRepository;
import com.liondance.liondance_backend.presentationlayer.Event.EventResponseModel;
import com.liondance.liondance_backend.utils.EntityModelUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Flux<EventResponseModel> getAllEvents() {
        return eventRepository.findAll()
                .map(EntityModelUtil::toEventResponseModel);
    }
}
