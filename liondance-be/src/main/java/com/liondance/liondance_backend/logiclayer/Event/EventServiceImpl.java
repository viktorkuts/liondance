package com.liondance.liondance_backend.logiclayer.Event;

import com.liondance.liondance_backend.datalayer.Event.EventPrivacy;
import com.liondance.liondance_backend.datalayer.Event.EventRepository;
import com.liondance.liondance_backend.datalayer.Event.EventStatus;
import com.liondance.liondance_backend.datalayer.Feedback.Feedback;
import com.liondance.liondance_backend.datalayer.Feedback.FeedbackRepository;
import com.liondance.liondance_backend.datalayer.Notification.NotificationType;
import com.liondance.liondance_backend.datalayer.User.Role;
import com.liondance.liondance_backend.datalayer.User.User;
import com.liondance.liondance_backend.logiclayer.Notification.NotificationService;
import com.liondance.liondance_backend.logiclayer.User.UserService;
import com.liondance.liondance_backend.presentationlayer.Event.EventDisplayDTO;
import com.liondance.liondance_backend.presentationlayer.Event.EventRequestModel;
import com.liondance.liondance_backend.presentationlayer.Event.EventResponseModel;
import com.liondance.liondance_backend.presentationlayer.Feedback.FeedbackRequestModel;
import com.liondance.liondance_backend.presentationlayer.Feedback.FeedbackResponseModel;
import com.liondance.liondance_backend.presentationlayer.User.UserRolePatchRequestModel;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.UUID;

import static com.liondance.liondance_backend.datalayer.Event.EventStatus.PENDING;
import static de.flapdoodle.os.Platform.logger;

@Slf4j
@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final NotificationService notificationService;
    private final UserService userService;
    private final FeedbackRepository feedbackRepository;

    public EventServiceImpl(EventRepository eventRepository, NotificationService notificationService, UserService userService, FeedbackRepository feedbackRepository) {
        this.eventRepository = eventRepository;
        this.notificationService = notificationService;
        this.userService = userService;
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public Flux<EventResponseModel> getAllEvents() {
        return eventRepository.findAll()
                .map(EventResponseModel::from);
    }

    @Override
    public Mono<EventResponseModel> bookEvent(Mono<EventRequestModel> eventRequestModel, User user) {
        return eventRequestModel
                .switchIfEmpty(Mono.error(new IllegalArgumentException("EventRequestModel cannot be null or empty")))
                .map(EventRequestModel::toEntity)
                .map(event -> {
                    event.setEventId(UUID.randomUUID().toString());
                    event.setEventStatus(PENDING);
                    event.setClientId(user.getUserId());
                    return event;
                })
                .doOnNext(event -> {
                    String message = new StringBuilder()
                            .append("We have received your request!")
                            .append("\nYour booking request is pending approval.")
                            .append("\nA staff member will validate your request details and contact you shortly.")
                            .append("\n\nThank you for choosing the LVH Lion Dance Team!")
                            .toString();

                    Boolean success = notificationService.sendMail(
                            user.getEmail(),
                            "LVH Lion Dance Team - Booking Pending Approval",
                            message,
                            NotificationType.EVENT_BOOKING
                    );

                    if (!success) {
                        throw new MailSendException("Failed to send email to " + user.getEmail());
                    }
                })
                .flatMap(event -> {
                    if(!user.getRoles().contains(Role.CLIENT)){
                        UserRolePatchRequestModel roles = new UserRolePatchRequestModel();
                        EnumSet<Role> userRoles = user.getRoles();
                        userRoles.add(Role.CLIENT);
                        roles.setRoles(userRoles.stream().toList());
                        return userService.updateUserRole(user.getUserId(), roles)
                                .then(Mono.just(event));
                    }
                    return Mono.just(event);
                })
                .flatMap(eventRepository::save)
                .map(EventResponseModel::from);
    }

    @Override
    public Mono<EventResponseModel> updateEventStatus(String eventId, Mono<EventStatus> eventStatus) {
        return eventRepository.findEventByEventId(eventId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Event not found")))
                .zipWith(eventStatus)
                .map(tuple -> {
                    tuple.getT1().setEventStatus(tuple.getT2());
                    return tuple.getT1();
                })
                .flatMap(eventRepository::save)
                .map(EventResponseModel::from);
    }

    @Override
    public Mono<EventResponseModel> getEventById(String eventId) {
        return eventRepository.findEventByEventId(eventId)
                .map(EventResponseModel::from);
    }

    @Override
    public Mono<EventResponseModel> rescheduleEvent(String eventId, Instant eventDateTime) {
        return eventRepository.findEventByEventId(eventId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Event not found")))
                .map(event -> {
                    event.setEventDateTime(eventDateTime);
                    return event;
                })
                .flatMap(event -> userService.getUserByUserId(event.getClientId())
                        .flatMap(user -> {
                            String message = new StringBuilder()
                                    .append("Your event has been rescheduled to ")
                                    .append(event.getEventDateTime().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a")))
                                    .append(".")
                                    .append("\n\nThank you for choosing the LVH Lion Dance Team!")
                                    .toString();

                            Boolean success = notificationService.sendMail(
                                    user.getEmail(),
                                    "LVH Lion Dance Team - Event Rescheduled",
                                    message,
                                    NotificationType.EVENT_RESCHEDULE
                            );

                            if (!success) {
                                return Mono.error(new MailSendException("Failed to send email to " + user.getEmail()));
                            }
                            return Mono.just(event);
                        })
                        .switchIfEmpty(Mono.error(new NotFoundException("User associated to event was not found"))))
                .flatMap(eventRepository::save)
                .map(EventResponseModel::from);
    }
  
    @Override
    public Flux<EventResponseModel> getEventsByClientId(String userId) {
       return eventRepository.findEventsByClientId(userId)
               .map(EventResponseModel::from).switchIfEmpty(Mono.error(new NotFoundException("No events found for client: " + userId)));
    }

    @Override
    public Flux<EventResponseModel> getFilteredEvents() {
        return eventRepository.findAll()
                .map(event ->
                        EventResponseModel.builder()
                                .eventId(event.getEventId())
                                .eventDateTime(event.getEventDateTime())
                                .eventType(event.getEventType())
                                .eventPrivacy(event.getEventPrivacy())
                                .venue(event.getEventPrivacy() == EventPrivacy.PUBLIC ? event.getVenue() : null)
                                .build()
                );
    }

    @Override
    public Mono<EventResponseModel> updateEventDetails(String eventId, EventRequestModel eventRequestModel) {
        return eventRepository.findEventByEventId(eventId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Event not found")))
                .map(event -> {
                    event.setVenue(eventRequestModel.getVenue());
                    event.setEventDateTime(eventRequestModel.getEventDateTime().atZone(ZoneOffset.UTC).toInstant());
                    event.setEventType(eventRequestModel.getEventType());
                    event.setPaymentMethod(eventRequestModel.getPaymentMethod());
                    event.setEventPrivacy(eventRequestModel.getEventPrivacy());
                    event.setSpecialRequest(eventRequestModel.getSpecialRequest());
                    return event;
                })
                .flatMap(event -> userService.getUserByUserId(event.getClientId())
                        .flatMap(user -> {
                            String message = new StringBuilder()
                                    .append("Your event details have been updated. Go take a look!")
                                    .append("\n\nThank you for choosing the LVH Lion Dance Team!")
                                    .toString();

                            Boolean success = notificationService.sendMail(
                                    user.getEmail(),
                                    "LVH Lion Dance Team - Event Details Updated",
                                    message,
                                    NotificationType.EVENT_UPDATE
                            );

                            if (!success) {
                                return Mono.error(new MailSendException("Failed to send email to " + user.getEmail()));
                            }
                            return Mono.just(event);
                        })
                        .switchIfEmpty(Mono.error(new NotFoundException("User associated to event was not found"))))
                .flatMap(eventRepository::save)
                .map(EventResponseModel::from);
    }

    @Override
    public Mono<FeedbackResponseModel> submitFeedback(String eventId, Mono<FeedbackRequestModel> feedbackRequestModel, User user) {
        logger.info("submitFeedback() called with eventId: {}", eventId);
        logger.info(" Roles: {}", user.getRoles());

        // Check if user has "CLIENT" role
        if (!user.getRoles().contains("CLIENT")) {
            logger.warn("Access Denied: User does not have CLIENT role");
            return Mono.error(new AccessDeniedException("Only clients can submit feedback."));
        }

        return eventRepository.findEventByEventId(eventId)
                .switchIfEmpty(Mono.error(new NotFoundException("Event not found with ID: " + eventId)))
                .flatMap(event -> {
                    logger.info("Event found: {}", event);

                    if (event.getEventStatus() != EventStatus.COMPLETED) {
                        logger.warn("Event is not completed, feedback submission denied");
                        return Mono.error(new IllegalStateException("Feedback can only be submitted for completed events."));
                    }

                    return feedbackRequestModel
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("FeedbackRequestModel cannot be null or empty")))
                            .doOnNext(request -> logger.info("Received Feedback: {}, Rating: {}", request.getFeedback(), request.getRating()))
                            .map(request -> Feedback.builder()
                                    .feedbackId(UUID.randomUUID().toString())
                                    .timestamp(Instant.now())
                                    .feedback(request.getFeedback())
                                    .rating(request.getRating())
                                    .eventId(eventId)
                                    .build()
                            )
                            .flatMap(feedback -> {
                                logger.info("Saving Feedback: {}", feedback);
                                return feedbackRepository.save(feedback);
                            })
                            .map(savedFeedback -> {
                                logger.info("Feedback saved successfully: {}", savedFeedback);
                                return FeedbackResponseModel.from(savedFeedback);
                            });
                })
                .doOnError(error -> logger.error("Error occurred in submitFeedback(): {}", error.getMessage(), error));
    }

}
