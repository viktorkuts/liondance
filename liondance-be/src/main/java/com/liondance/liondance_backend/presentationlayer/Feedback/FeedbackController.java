package com.liondance.liondance_backend.presentationlayer.Feedback;

import com.liondance.liondance_backend.datalayer.common.Visibility;
import com.liondance.liondance_backend.logiclayer.Feedback.FeedbackService;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/feedbacks")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
public class FeedbackController {
    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PreAuthorize("hasAuthority('STAFF')")
    @GetMapping("/event/{eventId}")
    public Flux<FeedbackResponseModel> getFeedbackByEventId(@PathVariable String eventId) {
        return feedbackService.getFeedbackByEventId(eventId)
                .onErrorResume(NotFoundException.class, e -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
                });
    }

    @PreAuthorize("#visibility == T(com.liondance.liondance_backend.datalayer.common.Visibility).PUBLIC || hasAuthority('STAFF')")
    @GetMapping()
    public Flux<FeedbackResponseModel> getFilteredFeedbacks(@RequestParam(name="visibility", defaultValue = "PUBLIC") Visibility visibility){
        return feedbackService.getFeedbackByVisibility(visibility);
    }

    @PreAuthorize("hasAuthority('STAFF')")
    @PatchMapping("{feedbackId}")
    public Mono<FeedbackResponseModel> updateEventFeedbackVisibility(@PathVariable String feedbackId, @RequestBody Visibility visibility){
        return feedbackService.updateEventFeedbackVisibility(feedbackId, visibility);
    }
}