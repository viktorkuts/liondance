package com.liondance.liondance_backend.logiclayer.Event;

import com.liondance.liondance_backend.datalayer.Event.EventRepository;
import com.liondance.liondance_backend.datalayer.Event.EventStatus;
import com.liondance.liondance_backend.datalayer.Notification.NotificationType;
import com.liondance.liondance_backend.logiclayer.Notification.NotificationService;
import com.liondance.liondance_backend.presentationlayer.Event.EventRequestModel;
import com.liondance.liondance_backend.presentationlayer.Event.EventResponseModel;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.liondance.liondance_backend.datalayer.Event.EventStatus.PENDING;

@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final NotificationService notificationService;

    public EventServiceImpl(EventRepository eventRepository, NotificationService notificationService) {
        this.eventRepository = eventRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Flux<EventResponseModel> getAllEvents() {
        return eventRepository.findAll()
                .map(EventResponseModel::from);
    }

    @Override
    public Mono<EventResponseModel> bookEvent(Mono<EventRequestModel> eventRequestModel) {
        return eventRequestModel
                .switchIfEmpty(Mono.error(new IllegalArgumentException("EventRequestModel cannot be null or empty")))
                .flatMap(request -> {
                    if (request.getEmail() == null || request.getEmail().isEmpty()) {
                        return Mono.error(new IllegalArgumentException("Email is required"));
                    }
                    return Mono.just(request);
                })                .map(EventRequestModel::toEntity)
                .map(event -> {
                    event.setId(UUID.randomUUID().toString());
                    event.setEventStatus(PENDING);
                    return event;
                })
                .doOnNext(event -> {
                    String message = new StringBuilder()
                            .append("We have received your request, ")
                            .append(event.getFirstName())
                            .append("!")
                            .append("\nYour booking request is pending approval.")
                            .append("\nA staff member will validate your request details and contact you shortly.")
                            .append("\n\nThank you for choosing the LVH Lion Dance Team!")
                            .toString();

                    Boolean success = notificationService.sendMail(
                            event.getEmail(),
                            "LVH Lion Dance Team - Booking Pending Approval",
                            message,
                            NotificationType.EVENT_BOOKING
                    );

                    if(!success){
                        throw new MailSendException("Failed to send email to " + event.getEmail());
                    }

                })
                .flatMap(eventRepository::save)
                .map(EventResponseModel::from);
    }

    @Override
    public Mono<EventResponseModel> updateEventStatus(String eventId, Mono<EventStatus> eventStatus) {
        return eventRepository.findById(eventId)
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
        return eventRepository.findById(eventId)
                .map(EventResponseModel::from);
    }

    @Override
    public Mono<EventResponseModel> rescheduleEvent(String eventId, Instant eventDateTime) {
        return eventRepository.findById(eventId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Event not found")))
                .map(event -> {
                    event.setEventDateTime(eventDateTime);
                    return event;
                })
                .flatMap(event -> {
                    String message = new StringBuilder()
                            .append("Your event has been rescheduled to ")
                            .append(event.getEventDateTime().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a")))
                            .append(".")
                            .append("\n\nThank you for choosing the LVH Lion Dance Team!")
                            .toString();

                    Boolean success = notificationService.sendMail(
                            event.getEmail(),
                            "LVH Lion Dance Team - Event Rescheduled",
                            message,
                            NotificationType.EVENT_RESCHEDULE
                    );

                    if(!success){
                        throw new MailSendException("Failed to send email to " + event.getEmail());
                    }

                    return Mono.just(event);
                })
                .flatMap(eventRepository::save)
                .map(EventResponseModel::from);
    }
    @Override
    public Flux<EventResponseModel> getEventsByEmail(String email) {
       return eventRepository.findEventsByEmail(email)
               .map(EventResponseModel::from).switchIfEmpty(Mono.error(new NotFoundException("No events found for email: " + email)));
    }

    @Override
    public Mono<EventResponseModel> updateEventDetails(String eventId, EventRequestModel eventRequestModel) {
        return eventRepository.findById(eventId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Event not found")))
                .map(event -> {
                    event.setFirstName(eventRequestModel.getFirstName());
                    event.setLastName(eventRequestModel.getLastName());
                    event.setEmail(eventRequestModel.getEmail());
                    event.setPhone(eventRequestModel.getPhone());
                    event.setAddress(eventRequestModel.getAddress());
                    event.setEventDateTime(eventRequestModel.getEventDateTime().atZone(ZoneOffset.UTC).toInstant());
                    event.setEventType(eventRequestModel.getEventType());
                    event.setPaymentMethod(eventRequestModel.getPaymentMethod());
                    event.setSpecialRequest(eventRequestModel.getSpecialRequest());
                    return event;
                })
                .flatMap(event -> {
                    String message = new StringBuilder()
                            .append("Your event details have been updated. Go take a look!")
                            .append("\n\nThank you for choosing the LVH Lion Dance Team!")
                            .toString();

                    Boolean success = notificationService.sendMail(
                            event.getEmail(),
                            "LVH Lion Dance Team - Event Details Updated",
                            message,
                            NotificationType.EVENT_UPDATE
                    );

                    if (!success) {
                        throw new MailSendException("Failed to send email to " + event.getEmail());
                    }

                    return Mono.just(event);
                })
                .flatMap(eventRepository::save)
                .map(EventResponseModel::from);
    }
}
