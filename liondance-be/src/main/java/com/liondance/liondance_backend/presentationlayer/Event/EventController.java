package com.liondance.liondance_backend.presentationlayer.Event;

import com.liondance.liondance_backend.datalayer.Event.EventStatus;
import com.liondance.liondance_backend.logiclayer.Event.EventService;
import com.liondance.liondance_backend.logiclayer.User.UserService;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/events")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
public class EventController {
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private final EventService eventService;
    private final UserService userService;

    public EventController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping
    public Flux<EventResponseModel> getAllEvents() {
        return eventService.getAllEvents();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = "application/json")
    public Mono<ResponseEntity<EventResponseModel>> bookEvent(@Valid @RequestBody Mono<EventRequestModel> eventRequestModel, @AuthenticationPrincipal JwtAuthenticationToken jwt) {
        return userService.validate(jwt.getName())
                .flatMap(user ->
                    eventService.bookEvent(eventRequestModel, user)
                            .map(eventResponseModel -> ResponseEntity.status(HttpStatus.CREATED).body(eventResponseModel))
                );
    }

    @PreAuthorize("hasAuthority('STAFF')")
    @PatchMapping("/{eventId}/status")
    public Mono<ResponseEntity<EventResponseModel>> updateEventStatus(@PathVariable String eventId, @RequestBody Mono<Map<String, String>> requestBody) {
        return requestBody
                .flatMap(body -> {
                    String status = body.get("eventStatus");
                    if (status == null || status.isEmpty()) {
                        return Mono.error(new IllegalArgumentException("Status cannot be null or empty"));
                    }
                    EventStatus eventStatus;
                    try {
                        eventStatus = EventStatus.valueOf(status.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        return Mono.error(new IllegalArgumentException("Invalid status value: " + status));
                    }
                    return eventService.updateEventStatus(eventId, Mono.just(eventStatus));
                })
                .map(eventResponseModel -> ResponseEntity.ok().body(eventResponseModel));
    }

    @PreAuthorize("hasAuthority('STAFF')")
    @GetMapping("/{eventId}")
    public Mono<ResponseEntity<EventResponseModel>> getEventById(@PathVariable String eventId) {
        return eventService.getEventById(eventId)
                .map(eventResponseModel -> ResponseEntity.ok().body(eventResponseModel));
    }

    @PatchMapping("/{eventId}/date")
    public Mono<ResponseEntity<EventResponseModel>> rescheduleEvent(@PathVariable String eventId, @RequestBody Mono<Map<String, String>> requestBody) {
        return requestBody
                .flatMap(body -> {
                    Instant date = Instant.parse(body.get("eventDateTime"));
                    if (date == null) {
                        return Mono.error(new IllegalArgumentException("Date cannot be null or empty"));
                    }
                    return eventService.rescheduleEvent(eventId, date);
                })
                .map(eventResponseModel -> ResponseEntity.ok().body(eventResponseModel));
    }

    @GetMapping("/filtered-events")
    public Flux<EventResponseModel> getFilteredEvents() {
        return eventService.getFilteredEvents();
    }

    @PutMapping("/{eventId}")
    public Mono<EventResponseModel> updateEventDetails(@PathVariable String eventId, @Valid @RequestBody EventRequestModel eventRequestModel) {
        return eventService.updateEventDetails(eventId, eventRequestModel);
    }
}