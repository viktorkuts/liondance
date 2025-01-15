package com.liondance.liondance_backend.datalayer.Event;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventRepository extends ReactiveMongoRepository<Event, String> {
    Flux<Event> findEventsByEmail(String email);
}
