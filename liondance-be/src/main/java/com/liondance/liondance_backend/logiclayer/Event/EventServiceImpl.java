package com.liondance.liondance_backend.logiclayer.Event;

import com.liondance.liondance_backend.datalayer.Event.EventRepository;
import com.liondance.liondance_backend.datalayer.Notification.NotificationType;
import com.liondance.liondance_backend.logiclayer.Notification.NotificationService;
import com.liondance.liondance_backend.presentationlayer.Event.EventRequestModel;
import com.liondance.liondance_backend.presentationlayer.Event.EventResponseModel;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
                .map(EventRequestModel::toEntity)
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
}
