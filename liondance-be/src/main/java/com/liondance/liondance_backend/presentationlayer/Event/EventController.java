package com.liondance.liondance_backend.presentationlayer.Event;

import com.liondance.liondance_backend.datalayer.Event.EventStatus;
import com.liondance.liondance_backend.logiclayer.Event.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/events")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
public class EventController {
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public Flux<EventResponseModel> getAllEvents() {
        return eventService.getAllEvents();
    }

    @PostMapping(consumes = "application/json")
    public Mono<ResponseEntity<EventResponseModel>> bookEvent(@Valid @RequestBody Mono<EventRequestModel> eventRequestModel) {
        return eventService.bookEvent(eventRequestModel)
                .map(eventResponseModel -> ResponseEntity.status(HttpStatus.CREATED).body(eventResponseModel));
    }

    @PatchMapping("/{eventId}/status")
    public Mono<ResponseEntity<EventResponseModel>> updateEventStatus(@PathVariable String eventId, @RequestBody Mono<Map<String, String>> requestBody) {
        return requestBody
                .flatMap(body -> {
                    logger.info("Received request body: {}", body);
                    String status = body.get("eventStatus");
                    logger.info("Extracted status: {}", status);
                    if (status == null || status.isEmpty()) {
                        logger.error("Status is null or empty");
                        return Mono.error(new IllegalArgumentException("Status cannot be null or empty"));
                    }
                    EventStatus eventStatus;
                    try {
                        eventStatus = EventStatus.valueOf(status.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        logger.error("Invalid status value: {}", status, e);
                        return Mono.error(new IllegalArgumentException("Invalid status value: " + status));
                    }
                    return eventService.updateEventStatus(eventId, Mono.just(eventStatus));
                })
                .map(eventResponseModel -> ResponseEntity.ok().body(eventResponseModel));
    }

    @GetMapping("/{eventId}")
    public Mono<ResponseEntity<EventResponseModel>> getEventById(@PathVariable String eventId) {
        return eventService.getEventById(eventId)
                .map(eventResponseModel -> ResponseEntity.ok().body(eventResponseModel));
    }
}