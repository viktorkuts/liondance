package com.liondance.liondance_backend.datalayer.Event;

import com.liondance.liondance_backend.presentationlayer.Event.EventDisplayDTO;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

public interface EventRepository extends ReactiveMongoRepository<Event, String> {
    Flux<Event> findEventsByEmail(String email);

}
