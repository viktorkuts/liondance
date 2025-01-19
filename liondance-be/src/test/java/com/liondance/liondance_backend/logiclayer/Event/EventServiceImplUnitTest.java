package com.liondance.liondance_backend.logiclayer.Event;

import com.liondance.liondance_backend.datalayer.Event.Event;
import com.liondance.liondance_backend.datalayer.Event.EventRepository;
import com.liondance.liondance_backend.datalayer.Event.EventType;
import com.liondance.liondance_backend.datalayer.Event.PaymentMethod;
import com.liondance.liondance_backend.datalayer.Notification.NotificationType;
import com.liondance.liondance_backend.datalayer.common.Address;
import com.liondance.liondance_backend.logiclayer.Notification.NotificationService;
import com.liondance.liondance_backend.presentationlayer.Event.EventRequestModel;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.mail.MailSendException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import static com.liondance.liondance_backend.datalayer.Event.EventStatus.PENDING;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventServiceImplUnitTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event event1;
    private Event event2;

    @BeforeEach
    void setUp() {
        event1 = Event.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("liondance@yopmail.com")
                .phone("1234567890")
                .address(
                        new Address(
                                "1234 Main St.",
                                "Springfield",
                                "Quebec",
                                "J2X 2J4")
                )
                .eventDateTime(Instant.now())
                .eventType(EventType.WEDDING)
                .paymentMethod(PaymentMethod.CASH)
                .specialRequest("Special request")
                .build();

        event2 = Event.builder()
                .firstName("Robert")
                .middleName("John")
                .lastName("DeNiro")
                .email("liondance@yopmail.com")
                .phone("1234567890")
                .address(
                        new Address(
                                "1234 Main St.",
                                "Springfield",
                                "Quebec",
                                "J2X 2J4")
                )
                .eventDateTime(Instant.now())
                .eventType(EventType.WEDDING)
                .paymentMethod(PaymentMethod.CASH)
                .specialRequest("Special request")
                .build();
    }

    @Test
    void whenGetAllEvents_thenReturnAllEvents() {
        Mockito.when(eventRepository.findAll())
                .thenReturn(Flux.just(event1, event2));

        StepVerifier.create(eventService.getAllEvents())
                .expectNextMatches(eventResponseModel -> {
                    Event event = Event.builder().build();
                    BeanUtils.copyProperties(eventResponseModel, event);
                    return event.equals(event1);
                })
                .expectNextMatches(eventResponseModel -> {
                    Event event = Event.builder().build();
                    BeanUtils.copyProperties(eventResponseModel, event);
                    return event.equals(event2);
                })
                .verifyComplete();
    }

    @Test
    void whenBookEvent_Successful_thenReturnEventResponse() {
        EventRequestModel requestModel = EventRequestModel.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("liondance@yopmail.com")
                .phone("1234567890")
                .address(
                        new Address(
                                "1234 Main St.",
                                "Springfield",
                                "Quebec",
                                "J2X 2J4")
                )
                .eventDateTime(LocalDate.now().atTime(LocalTime.NOON))
                .eventType(EventType.WEDDING)
                .paymentMethod(PaymentMethod.CASH)
                .specialRequest("Special request")
                .build();

        Mockito.when(eventRepository.save(any(Event.class)))
                .thenReturn(Mono.just(event1));
        Mockito.when(notificationService.sendMail(anyString(), anyString(), anyString(), any(NotificationType.class)))
                .thenReturn(true);

        StepVerifier.create(eventService.bookEvent(Mono.just(requestModel)))
                .expectNextMatches(eventResponseModel -> {
                    Event event = Event.builder().build();
                    BeanUtils.copyProperties(eventResponseModel, event);
                    return event.equals(event1);
                })
                .verifyComplete();

        verify(notificationService, times(1)).sendMail(anyString(), anyString(), anyString(), any(NotificationType.class));
    }

    @Test
    void whenBookEvent_FailedToSendEmail_thenThrowMailSendException() {
        EventRequestModel requestModel = EventRequestModel.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("liondance@yopmail.com")
                .phone("1234567890")
                .address(
                        new Address(
                                "1234 Main St.",
                                "Springfield",
                                "Quebec",
                                "J2X 2J4")
                )
                .eventDateTime(LocalDate.now().atTime(LocalTime.NOON))
                .eventType(EventType.WEDDING)
                .paymentMethod(PaymentMethod.CASH)
                .specialRequest("Special request")
                .build();

        Mockito.when(notificationService.sendMail(anyString(), anyString(), anyString(), any(NotificationType.class)))
                .thenReturn(false);

        StepVerifier.create(eventService.bookEvent(Mono.just(requestModel)))
                .expectError(MailSendException.class)
                .verify();

        verify(notificationService, times(1)).sendMail(anyString(), anyString(), anyString(), any(NotificationType.class));
    }

    @Test
    void whenBookEvent_InvalidEmail_thenThrowMailSendException() {
        EventRequestModel requestModel = EventRequestModel.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("invalid-email")
                .phone("1234567890")
                .address(
                        new Address(
                                "1234 Main St.",
                                "Springfield",
                                "Quebec",
                                "J2X 2J4")
                )
                .eventDateTime(LocalDate.now().atTime(LocalTime.NOON))
                .eventType(EventType.WEDDING)
                .paymentMethod(PaymentMethod.CASH)
                .specialRequest("Special request")
                .build();

        Mockito.when(notificationService.sendMail(anyString(), anyString(), anyString(), any(NotificationType.class)))
                .thenThrow(new MailSendException("Invalid email address"));

        StepVerifier.create(eventService.bookEvent(Mono.just(requestModel)))
                .expectError(MailSendException.class)
                .verify();

        verify(notificationService, times(1)).sendMail(anyString(), anyString(), anyString(), any(NotificationType.class));
    }

    @Test
    void whenBookEvent_EmptyRequestModel_thenThrowIllegalArgumentException() {
        EventRequestModel requestModel = EventRequestModel.builder().build();

        StepVerifier.create(eventService.bookEvent(Mono.just(requestModel)))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void whenGetAllEvents_NoEvents_thenReturnEmptyList() {
        Mockito.when(eventRepository.findAll())
                .thenReturn(Flux.empty());

        StepVerifier.create(eventService.getAllEvents())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void whenUpdateEventStatus_Successful_thenReturnEventResponse() {
        Mockito.when(eventRepository.findById(anyString()))
                .thenReturn(Mono.just(event1));
        Mockito.when(eventRepository.save(any(Event.class)))
                .thenReturn(Mono.just(event1));

        StepVerifier.create(eventService.updateEventStatus("1", Mono.just(PENDING)))
                .expectNextMatches(eventResponseModel -> {
                    Event event = Event.builder().build();
                    BeanUtils.copyProperties(eventResponseModel, event);
                    return event.equals(event1);
                })
                .verifyComplete();
    }

    @Test
    void whenUpdateEventStatus_EventNotFound_thenThrowIllegalArgumentException() {
        Mockito.when(eventRepository.findById(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(eventService.updateEventStatus("1", Mono.just(PENDING)))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void whenGetEventById_Successful_thenReturnEventResponse() {
        Mockito.when(eventRepository.findById(anyString()))
                .thenReturn(Mono.just(event1));

        StepVerifier.create(eventService.getEventById("1"))
                .expectNextMatches(eventResponseModel -> {
                    Event event = Event.builder().build();
                    BeanUtils.copyProperties(eventResponseModel, event);
                    return event.equals(event1);
                })
                .verifyComplete();
    }

    @Test
    void whenRescheduleEvent_Successful_thenReturnEventResponse() {
        Mockito.when(eventRepository.findById(anyString()))
                .thenReturn(Mono.just(event1));
        Mockito.when(eventRepository.save(any(Event.class)))
                .thenReturn(Mono.just(event1));
        Mockito.when(notificationService.sendMail(anyString(), anyString(), anyString(), any(NotificationType.class)))
                .thenReturn(true);

        StepVerifier.create(eventService.rescheduleEvent("1", Instant.now()))
                .expectNextMatches(eventResponseModel -> {
                    Event event = Event.builder().build();
                    BeanUtils.copyProperties(eventResponseModel, event);
                    return event.equals(event1);
                })
                .verifyComplete();
    }

    @Test
    void whenRescheduleEvent_EventNotFound_thenThrowIllegalArgumentException() {
        Mockito.when(eventRepository.findById(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(eventService.rescheduleEvent("1", Instant.now()))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void whenGetEventsByEmail_thenReturnEvents() {
        Mockito.when(eventRepository.findEventsByEmail("liondance@yopmail.com"))
                .thenReturn(Flux.just(event1, event2));

        StepVerifier.create(eventService.getEventsByEmail("liondance@yopmail.com"))
                .expectNextMatches(eventResponseModel -> {
                    Event event = Event.builder().build();
                    BeanUtils.copyProperties(eventResponseModel, event);
                    return event.equals(event1);
                })
                .expectNextMatches(eventResponseModel -> {
                    Event event = Event.builder().build();
                    BeanUtils.copyProperties(eventResponseModel, event);
                    return event.equals(event2);
                })
                .verifyComplete();
    }

    @Test
    void whenRescheduleEvent_FailedToSendEmail_thenThrowMailSendException() {
        Mockito.when(eventRepository.findById(anyString()))
                .thenReturn(Mono.just(event1));
        Mockito.when(notificationService.sendMail(anyString(), anyString(), anyString(), any(NotificationType.class)))
                .thenReturn(false);

        StepVerifier.create(eventService.rescheduleEvent("1", Instant.now()))
                .expectError(MailSendException.class)
                .verify();
    }

    @Test
    void whenUpdateEventDetails_Successful_thenReturnEventResponse() {
        EventRequestModel requestModel = EventRequestModel.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("liondance@yopmail.com")
                .phone("1234567890")
                .address(new Address("1234 Main St.", "Springfield", "Quebec", "J2X 2J4"))
                .eventDateTime(LocalDate.now().atTime(LocalTime.NOON))
                .eventType(EventType.WEDDING)
                .paymentMethod(PaymentMethod.CASH)
                .specialRequest("Special request")
                .build();

        Mockito.when(eventRepository.findById(anyString()))
                .thenReturn(Mono.just(event1));
        Mockito.when(eventRepository.save(any(Event.class)))
                .thenReturn(Mono.just(event1));
        Mockito.when(notificationService.sendMail(anyString(), anyString(), anyString(), any(NotificationType.class)))
                .thenReturn(true);

        StepVerifier.create(eventService.updateEventDetails("1", requestModel))
                .expectNextMatches(eventResponseModel -> {
                    Event event = Event.builder().build();
                    BeanUtils.copyProperties(eventResponseModel, event);
                    return event.equals(event1);
                })
                .verifyComplete();

        verify(notificationService, times(1)).sendMail(anyString(), anyString(), anyString(), any(NotificationType.class));
    }

    @Test
    void whenUpdateEventDetails_EventNotFound_thenThrowIllegalArgumentException() {
        EventRequestModel requestModel = EventRequestModel.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("liondance@yopmail.com")
                .phone("1234567890")
                .address(new Address("1234 Main St.", "Springfield", "Quebec", "J2X 2J4"))
                .eventDateTime(LocalDate.now().atTime(LocalTime.NOON))
                .eventType(EventType.WEDDING)
                .paymentMethod(PaymentMethod.CASH)
                .specialRequest("Special request")
                .build();

        Mockito.when(eventRepository.findById(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(eventService.updateEventDetails("1", requestModel))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void whenUpdateEventDetails_FailedToSendEmail_thenThrowMailSendException() {
        EventRequestModel requestModel = EventRequestModel.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("liondance@yopmail.com")
                .phone("1234567890")
                .address(new Address("1234 Main St.", "Springfield", "Quebec", "J2X 2J4"))
                .eventDateTime(LocalDate.now().atTime(LocalTime.NOON))
                .eventType(EventType.WEDDING)
                .paymentMethod(PaymentMethod.CASH)
                .specialRequest("Special request")
                .build();

        Mockito.when(eventRepository.findById(anyString()))
                .thenReturn(Mono.just(event1));
        Mockito.when(notificationService.sendMail(anyString(), anyString(), anyString(), any(NotificationType.class)))
                .thenReturn(false);

        StepVerifier.create(eventService.updateEventDetails("1", requestModel))
                .expectError(MailSendException.class)
                .verify();

        verify(notificationService, times(1)).sendMail(anyString(), anyString(), anyString(), any(NotificationType.class));
    }

    @Test
    void whenGetEventsByInvalidEmail_thenReturnNotFound() {
        Mockito.when(eventRepository.findEventsByEmail("invalid-email"))
                .thenReturn(Flux.empty());

        StepVerifier.create(eventService.getEventsByEmail("invalid-email"))
                .expectError(NotFoundException.class)
                .verify();
    }
}