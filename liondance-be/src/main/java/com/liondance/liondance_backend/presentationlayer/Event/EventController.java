package com.liondance.liondance_backend.presentationlayer.Event;

import com.liondance.liondance_backend.logiclayer.Event.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/events")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping(
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<EventResponseModel> getAllEvents() {
        return eventService.getAllEvents();
    }

    @PostMapping(consumes = "application/json")
    public Mono<ResponseEntity<EventResponseModel>> bookEvent(@Valid @RequestBody Mono<EventRequestModel> eventRequestModel) {
        return eventService.bookEvent(eventRequestModel)
                .map(eventResponseModel -> ResponseEntity.status(HttpStatus.CREATED).body(eventResponseModel));
    }
}
