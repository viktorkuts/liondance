package com.liondance.liondance_backend.logiclayer.Event;

import com.liondance.liondance_backend.datalayer.Event.*;
import com.liondance.liondance_backend.datalayer.Feedback.Feedback;
import com.liondance.liondance_backend.datalayer.Feedback.FeedbackRepository;
import com.liondance.liondance_backend.datalayer.Notification.NotificationType;
import com.liondance.liondance_backend.datalayer.User.Role;
import com.liondance.liondance_backend.datalayer.User.User;
import com.liondance.liondance_backend.datalayer.User.UserRepository;
import com.liondance.liondance_backend.logiclayer.Notification.NotificationService;
import com.liondance.liondance_backend.logiclayer.User.UserService;
import com.liondance.liondance_backend.presentationlayer.Event.EventRequestModel;
import com.liondance.liondance_backend.presentationlayer.Event.EventResponseModel;
import com.liondance.liondance_backend.presentationlayer.Event.PerformerResponseModel;
import com.liondance.liondance_backend.presentationlayer.Event.PerformerStatusRequestModel;
import com.liondance.liondance_backend.presentationlayer.Feedback.FeedbackRequestModel;
import com.liondance.liondance_backend.presentationlayer.Feedback.FeedbackResponseModel;
import com.liondance.liondance_backend.presentationlayer.User.UserResponseModel;
import com.liondance.liondance_backend.presentationlayer.User.UserRolePatchRequestModel;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.liondance.liondance_backend.datalayer.Event.EventStatus.CONFIRMED;
import static com.liondance.liondance_backend.datalayer.Event.EventStatus.PENDING;
import static de.flapdoodle.os.Platform.logger;

@Slf4j
@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final NotificationService notificationService;
    private final UserService userService;
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    @Value("${liondance.frontend.url}")
    private String frontendUrl;

    public EventServiceImpl(EventRepository eventRepository, NotificationService notificationService, UserService userService,
                            FeedbackRepository feedbackRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.notificationService = notificationService;
        this.userService = userService;
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
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
                .flatMap(tuple -> {
                    Event event = tuple.getT1();
                    event.setEventStatus(tuple.getT2());

                    switch (event.getEventStatus()) {
                        case CONFIRMED:
                        case CANCELLED:
                            return userService.getUserByUserId(event.getClientId())
                                    .flatMap(user -> {
                                        String statusMessage = event.getEventStatus() == CONFIRMED
                                                ? "Your event has been confirmed"
                                                : "Your event has been cancelled";

                                        String message = String.format("""
                                            Hello %s!
                                            
                                            We are writing to give you an update on your event booking:
                                            %s.
                                            
                                            Do not hesitate to contact us if you have any questions.
                                            
                                            Best regards,
                                            
                                            LVH Lion Dance Team
                                            945 Chemin de Chambly, Longueuil, QC, Canada, Quebec
                                            terry.chan@myliondance.com""",
                                                user.getFirstName(), statusMessage);

                                        Boolean success = notificationService.sendMail(
                                                user.getEmail(),
                                                "LVH Lion Dance Team - Event Status Update",
                                                message,
                                                NotificationType.EVENT_STATUS_UPDATE
                                        );
                                        if (!success) {
                                            throw new MailSendException("Failed to send email to " + user.getEmail());
                                        }
                                        return Mono.just(event);
                                    });
                        default:
                            return Mono.just(event);
                    }
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
//        if (!user.getRoles().contains("CLIENT")) {
//            logger.warn("Access Denied: User does not have CLIENT role");
//            return Mono.error(new AccessDeniedException("Only clients can submit feedback."));
//        }

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

    @Override
    public Mono<Void> requestFeedback(String eventId) {
        String frontendUrl = System.getenv("FRONTEND_URL");
        if (frontendUrl == null || frontendUrl.isEmpty()) {
            return Mono.error(new IllegalStateException("FRONTEND_URL environment variable is not set"));
        }

        return eventRepository.findEventByEventId(eventId)
                .switchIfEmpty(Mono.error(new NotFoundException("Event not found with ID: " + eventId)))
                .flatMap(event -> userRepository.findUserByUserId(event.getClientId())
                        .switchIfEmpty(Mono.error(new NotFoundException("User associated with event not found")))
                        .flatMap(user -> {
                            String message = new StringBuilder()
                                    .append("Hello! We would appreciate that you provide feedback for your recent event using the link below:\n\n")
                                    .append(frontendUrl)
                                    .append("\n\nRemember to log in to your account to access the feedback form!")
                                    .append("\n\nThank you!")

                                    .toString();

                            boolean success = notificationService.sendMail(
                                    user.getEmail(),
                                    "Feedback Request for Your Event",
                                    message,
                                    NotificationType.FEEDBACK_REQUEST
                            );

                            if (!success) {
                                return Mono.error(new MailSendException("Failed to send email to " + user.getEmail()));
                            }
                            return Mono.<Void>empty();
                        })
                );
    }

    @Override
    public Mono<EventResponseModel> assignPerformers(String eventId, List<String> performerIds) {
        return eventRepository.findEventByEventId(eventId)
                .switchIfEmpty(Mono.error(new NotFoundException("Event not found with ID: " + eventId)))
                .flatMap(event -> {
                            HashMap<String, PerformerStatus> performers = new HashMap<>(event.getPerformers());
                            performerIds.forEach(performerId -> {
                                performers.put(performerId, PerformerStatus.PENDING);
                            });
                            event.setPerformers(performers);
                            return Flux.fromIterable(performers.entrySet())
                                    .flatMap(entry ->
                                        userRepository.findUserByUserId(entry.getKey())
                                                .switchIfEmpty(Mono.error(new NotFoundException("User associated with performer not found")))
                                                .flatMap(user -> {
                                                    if(!entry.getValue().equals(PerformerStatus.PENDING)) return Mono.empty();
                                                    String message = new StringBuilder()
                                                            .append("You have been asked to participate at the event on ")
                                                            .append(event.getEventDateTime().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a")))
                                                            .append(". Use the link below to confirm:").append("\n " + frontendUrl + "/events/").append(eventId).append("/performers")
                                                            .toString();

                                                    boolean success = notificationService.sendMail(
                                                            user.getEmail(),
                                                            "Participation Request for Event",
                                                            message,
                                                            NotificationType.PERFORMANCE_REQUEST,
                                                            entry.getKey()

                                                    );

                                                    if (!success) {
                                                        return Mono.error(new MailSendException("Failed to send email to " + user.getEmail()));
                                                    }
                                                    return Mono.empty();
                                                })
                                    )
                                    .then(Mono.just(event));
                        })
                .flatMap(eventRepository::save)
                .map(EventResponseModel::from);
    }

    @Override
    public Mono<EventResponseModel> removePerformers(String eventId, List<String> performerIds) {
        return eventRepository.findEventByEventId(eventId)
                .switchIfEmpty(Mono.error(new NotFoundException("Event not found with ID: " + eventId)))
                .flatMap(event -> {
                    event.getPerformers().keySet().removeIf(performerIds::contains);
                    return Flux.fromIterable(performerIds)
                                    .flatMap(performerId -> userRepository.findUserByUserId(performerId)
                                            .switchIfEmpty(Mono.error(new NotFoundException("User associated with performer not found")))
                                            .flatMap(user -> {
                                                String message = new StringBuilder()
                                                        .append("You have been removed from the event on ")
                                                        .append(event.getEventDateTime().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a")))
                                                        .append(". If you have any questions, please contact us.")
                                                        .toString();

                                                boolean success = notificationService.sendMail(
                                                        user.getEmail(),
                                                        "Performance Cancellation for Event",
                                                        message,
                                                        NotificationType.PERFORMANCE_UPDATE
                                                );

                                                if (!success) {
                                                    return Mono.error(new MailSendException("Failed to send email to " + user.getEmail()));
                                                }
                                                return Mono.empty();
                                            }))
                                    .then(Mono.just(event));
                })
                .flatMap(eventRepository::save)
                .map(EventResponseModel::from);
    }

    @Override
    public Flux<PerformerResponseModel> getPerformers(String eventId) {
        return eventRepository.findEventByEventId(eventId)
                .switchIfEmpty(Mono.error(new NotFoundException("Event not found with ID: " + eventId)))
                .flatMap(e -> {
                    try{
                        return Mono.just(e.getPerformers());
                    } catch (NullPointerException ex) {
                        return Mono.empty();
                    }
                })
                .defaultIfEmpty(new HashMap<>())
                .flatMapMany(e -> Flux.fromIterable(e.entrySet())
                        .flatMap(entry -> userService.getUserByUserId(entry.getKey())
                                .flatMapMany(user -> {
                                    PerformerResponseModel model = new PerformerResponseModel();
                                    model.setPerformer(user);
                                    model.setStatus(entry.getValue());
                                    return Mono.just(model);
                                })
                        )
                );
    }

    @Scheduled(cron = "0 0 * * * *")
    private void sendPerformerReminder48h(){
        eventRepository.findAll()
                .filter(event ->
                        event.getEventStatus().equals(CONFIRMED)
                                &&
                        Instant.now().isBefore(event.getEventDateTime())
                                &&
                        Instant.now().isAfter(event.getEventDateTime().minus(48, ChronoUnit.HOURS))
                )
                .flatMap(e -> notificationService.getNotificationByAssociatedId(e.getEventId(), NotificationType.PERFORMANCE_REMINDER)
                        .switchIfEmpty(Mono.defer(() -> Flux.fromIterable(e.getPerformers().entrySet())
                                .flatMap(entry ->
                                        userRepository.findUserByUserId(entry.getKey())
                                                .flatMap(user -> {
                                                    String message = new StringBuilder()
                                                            .append("Performance Reminder\n")
                                                            .append(e.getEventDateTime().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a")))
                                                            .append(". If you have any questions, please contact us.")
                                                            .toString();

                                                    boolean success = notificationService.sendMail(
                                                            user.getEmail(),
                                                            "Upcoming Performance Reminder",
                                                            message,
                                                            NotificationType.PERFORMANCE_REMINDER,
                                                            e.getEventId()
                                                    );

                                                    if (!success) {
                                                        return Mono.error(new MailSendException("Failed to send email to " + user.getEmail()));
                                                    }
                                                    return Mono.empty();
                                                })
                                )
                                .then(Mono.empty())
                        ))
                        .flatMap(n -> Mono.empty())
                )
                .subscribe();
    }

    @Override
    public Flux<UserResponseModel> getAvailablePerformers(String eventId) {
        return eventRepository.findEventByEventId(eventId)
                .switchIfEmpty(Mono.error(new NotFoundException("Event not found with ID: " + eventId)))
                .flatMapMany(event -> {
                    Set<String> performers = event.getPerformers().keySet();
                    return userService.getAllUsers()
                            .filter(user -> user.getRoles().contains(Role.STAFF) || user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.STUDENT))
                            .filter(user -> !performers.contains(user.getUserId()));
                });
    }

    @Override
    public Mono<PerformerResponseModel> updatePerformerStatus(String eventId, String userId, PerformerStatusRequestModel requestModel) {
        return eventRepository.findEventByEventId(eventId)
                .switchIfEmpty(Mono.error(new NotFoundException("Event not found")))
                .map(e -> {
                    e.getPerformers().replace(userId, requestModel.getStatus());
                    return e;
                })
                .flatMap(eventRepository::save)
                .flatMap(e -> userService.getUserByUserId(userId))
                .map(u -> PerformerResponseModel.builder()
                        .status(requestModel.getStatus())
                        .performer(u)
                        .build());
    }
}
