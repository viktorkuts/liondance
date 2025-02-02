package com.liondance.liondance_backend.logiclayer.Event;

import com.liondance.liondance_backend.datalayer.Event.*;
import com.liondance.liondance_backend.datalayer.Notification.NotificationType;
import com.liondance.liondance_backend.datalayer.User.Client;
import com.liondance.liondance_backend.datalayer.User.Role;
import com.liondance.liondance_backend.datalayer.User.UserRepository;
import com.liondance.liondance_backend.datalayer.common.Address;
import com.liondance.liondance_backend.logiclayer.Notification.NotificationService;
import com.liondance.liondance_backend.logiclayer.User.UserService;
import com.liondance.liondance_backend.logiclayer.User.UserServiceImpl;
import com.liondance.liondance_backend.presentationlayer.Event.EventRequestModel;
import com.liondance.liondance_backend.presentationlayer.Event.EventResponseModel;
import com.liondance.liondance_backend.presentationlayer.User.UserResponseModel;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailSendException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.UUID;

import static com.liondance.liondance_backend.datalayer.Event.EventStatus.PENDING;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Disabled
@ExtendWith(MockitoExtension.class)
class EventServiceImplUnitTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event event1;
    private Event event2;

    private Client client1;
    private Client client2;

    @BeforeEach
    void setUp() {
        client1 = Client.builder()
                .userId(UUID.randomUUID().toString())
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
                .roles(EnumSet.of(Role.CLIENT))
                .build();
        client2 = Client.builder()
                .userId(UUID.randomUUID().toString())
                .firstName("John")
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
                .roles(EnumSet.of(Role.CLIENT))
                .build();

        event1 = Event.builder()
                .eventId(UUID.randomUUID().toString())
                .venue(
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
                .clientId(client1.getUserId())
                .build();

        event2 = Event.builder()
                .eventId(UUID.randomUUID().toString())
                .venue(
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
                .clientId(client2.getUserId())
                .build();
    }

    @Test
    void whenGetAllEvents_thenReturnAllEvents() {
        Mockito.when(eventRepository.findAll())
                .thenReturn(Flux.just(event1, event2));

        StepVerifier.create(eventService.getAllEvents())
                .expectNextMatches(eventResponseModel -> EventResponseModel.from(event1).equals(eventResponseModel))
                .expectNextMatches(eventResponseModel -> EventResponseModel.from(event2).equals(eventResponseModel))
                .verifyComplete();
    }

    @Test
    void whenBookEvent_Successful_thenReturnEventResponse() {
        EventRequestModel requestModel = EventRequestModel.builder()
                .venue(
                        new Address(
                                "1234 Main St.",
                                "Springfield",
                                "Quebec",
                                "J2X 2J4")
                )
                .eventDateTime(Instant.from(LocalDate.now().atTime(LocalTime.NOON)))
                .eventType(EventType.WEDDING)
                .paymentMethod(PaymentMethod.CASH)
                .specialRequest("Special request")
                .build();

        Mockito.when(eventRepository.save(any(Event.class)))
                .thenReturn(Mono.just(event1));
        Mockito.when(notificationService.sendMail(anyString(), anyString(), anyString(), any(NotificationType.class)))
                .thenReturn(true);

        StepVerifier.create(eventService.bookEvent(Mono.just(requestModel), client1))
                .expectNextMatches(eventResponseModel -> EventResponseModel.from(event1).equals(eventResponseModel))
                .verifyComplete();

        verify(notificationService, times(1)).sendMail(anyString(), anyString(), anyString(), any(NotificationType.class));
    }

    @Test
    void whenBookEvent_FailedToSendEmail_thenThrowMailSendException() {
        EventRequestModel requestModel = EventRequestModel.builder()
                .venue(
                        new Address(
                                "1234 Main St.",
                                "Springfield",
                                "Quebec",
                                "J2X 2J4")
                )
                .eventDateTime(Instant.from(LocalDate.now().atTime(LocalTime.NOON)))
                .eventType(EventType.WEDDING)
                .paymentMethod(PaymentMethod.CASH)
                .specialRequest("Special request")
                .build();

        Mockito.when(notificationService.sendMail(anyString(), anyString(), anyString(), any(NotificationType.class)))
                .thenReturn(false);

        StepVerifier.create(eventService.bookEvent(Mono.just(requestModel), client1))
                .expectError(MailSendException.class)
                .verify();

        verify(notificationService, times(1)).sendMail(anyString(), anyString(), anyString(), any(NotificationType.class));
    }

    @Test
    void whenBookEvent_InvalidEmail_thenThrowMailSendException() {
        EventRequestModel requestModel = EventRequestModel.builder()
                .venue(
                        new Address(
                                "1234 Main St.",
                                "Springfield",
                                "Quebec",
                                "J2X 2J4")
                )
                .eventDateTime(Instant.from(LocalDate.now().atTime(LocalTime.NOON)))
                .eventType(EventType.WEDDING)
                .paymentMethod(PaymentMethod.CASH)
                .specialRequest("Special request")
                .build();

        Mockito.when(notificationService.sendMail(anyString(), anyString(), anyString(), any(NotificationType.class)))
                .thenThrow(new MailSendException("Invalid email address"));

        StepVerifier.create(eventService.bookEvent(Mono.just(requestModel), client2))
                .expectError(MailSendException.class)
                .verify();

        verify(notificationService, times(1)).sendMail(anyString(), anyString(), anyString(), any(NotificationType.class));
    }

    @Test
    void whenBookEvent_EmptyRequestModel_thenThrowIllegalArgumentException() {
        EventRequestModel requestModel = EventRequestModel.builder().build();

        StepVerifier.create(eventService.bookEvent(Mono.just(requestModel), client2))
                .expectError(MailSendException.class)
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
        Mockito.when(eventRepository.findEventByEventId(anyString()))
                .thenReturn(Mono.just(event1));
        Mockito.when(eventRepository.save(any(Event.class)))
                .thenReturn(Mono.just(event1));

        StepVerifier.create(eventService.updateEventStatus("1", Mono.just(PENDING)))
                .expectNextMatches(eventResponseModel -> EventResponseModel.from(event1).equals(eventResponseModel))
                .verifyComplete();
    }

    @Test
    void whenUpdateEventStatus_EventNotFound_thenThrowIllegalArgumentException() {
        Mockito.when(eventRepository.findEventByEventId(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(eventService.updateEventStatus("1", Mono.just(PENDING)))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void whenGetEventById_Successful_thenReturnEventResponse() {
        Mockito.when(eventRepository.findEventByEventId(anyString()))
                .thenReturn(Mono.just(event1));

        StepVerifier.create(eventService.getEventById("1"))
                .expectNextMatches(eventResponseModel -> EventResponseModel.from(event1).equals(eventResponseModel))
                .verifyComplete();
    }

    @Test
    void whenRescheduleEvent_Successful_thenReturnEventResponse() {
        Mockito.when(eventRepository.findEventByEventId(anyString()))
                .thenReturn(Mono.just(event1));
        Mockito.when(eventRepository.save(any(Event.class)))
                .thenReturn(Mono.just(event1));
        Mockito.when(notificationService.sendMail(anyString(), anyString(), anyString(), any(NotificationType.class)))
                .thenReturn(true);
        Mockito.when(userService.getUserByUserId(anyString()))
                .thenReturn(Mono.just(UserResponseModel.from(client1)));

        StepVerifier.create(eventService.rescheduleEvent("1", Instant.now()))
                .expectNextMatches(eventResponseModel -> EventResponseModel.from(event1).equals(eventResponseModel))
                .verifyComplete();
    }

    @Test
    void whenRescheduleEvent_EventNotFound_thenThrowIllegalArgumentException() {
        Mockito.when(eventRepository.findEventByEventId(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(eventService.rescheduleEvent("1", Instant.now()))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void whenGetEventsByClientId_thenReturnEvents() {
        Mockito.when(eventRepository.findEventsByClientId(client1.getUserId()))
                .thenReturn(Flux.just(event1, event2));
        StepVerifier.create(eventService.getEventsByClientId(client1.getUserId()))
                .expectNextMatches(eventResponseModel -> EventResponseModel.from(event1).equals(eventResponseModel))
                .verifyComplete();
    }

    @Test
    void whenRescheduleEvent_FailedToSendEmail_thenThrowMailSendException() {
        Mockito.when(eventRepository.findEventByEventId(anyString()))
                .thenReturn(Mono.just(event1));
        Mockito.when(notificationService.sendMail(anyString(), anyString(), anyString(), any(NotificationType.class)))
                .thenReturn(false);
        Mockito.when(userService.getUserByUserId(anyString())).thenReturn(Mono.just(UserResponseModel.from(client1)));

        StepVerifier.create(eventService.rescheduleEvent("1", Instant.now()))
                .expectError(MailSendException.class)
                .verify();
    }

    @Test
    void whenUpdateEventDetails_Successful_thenReturnEventResponse() {
        EventRequestModel requestModel = EventRequestModel.builder()
                .venue(new Address("1234 Main St.", "Springfield", "Quebec", "J2X 2J4"))
                .eventDateTime(Instant.from(LocalDate.now().atTime(LocalTime.NOON)))
                .eventType(EventType.WEDDING)
                .paymentMethod(PaymentMethod.CASH)
                .specialRequest("Special request")
                .build();

        Mockito.when(eventRepository.findEventByEventId(anyString()))
                .thenReturn(Mono.just(event1));
        Mockito.when(eventRepository.save(any(Event.class)))
                .thenReturn(Mono.just(event1));
        Mockito.when(notificationService.sendMail(anyString(), anyString(), anyString(), any(NotificationType.class)))
                .thenReturn(true);
        Mockito.when(userService.getUserByUserId(anyString())).thenReturn(Mono.just(UserResponseModel.from(client1)));

        StepVerifier.create(eventService.updateEventDetails("1", requestModel))
                .expectNextMatches(eventResponseModel -> EventResponseModel.from(event1).equals(eventResponseModel))
                .verifyComplete();

        verify(notificationService, times(1)).sendMail(anyString(), anyString(), anyString(), any(NotificationType.class));
    }

    @Test
    void whenUpdateEventDetails_EventNotFound_thenThrowIllegalArgumentException() {
        EventRequestModel requestModel = EventRequestModel.builder()
                .venue(new Address("1234 Main St.", "Springfield", "Quebec", "J2X 2J4"))
                .eventDateTime(Instant.from(LocalDate.now().atTime(LocalTime.NOON)))
                .eventType(EventType.WEDDING)
                .paymentMethod(PaymentMethod.CASH)
                .specialRequest("Special request")
                .build();

        Mockito.when(eventRepository.findEventByEventId(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(eventService.updateEventDetails("1", requestModel))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void whenUpdateEventDetails_FailedToSendEmail_thenThrowMailSendException() {
        EventRequestModel requestModel = EventRequestModel.builder()
                .venue(new Address("1234 Main St.", "Springfield", "Quebec", "J2X 2J4"))
                .eventDateTime(Instant.from(LocalDate.now().atTime(LocalTime.NOON)))
                .eventType(EventType.WEDDING)
                .paymentMethod(PaymentMethod.CASH)
                .specialRequest("Special request")
                .build();

        Mockito.when(eventRepository.findEventByEventId(anyString()))
                .thenReturn(Mono.just(event1));
        Mockito.when(notificationService.sendMail(anyString(), anyString(), anyString(), any(NotificationType.class)))
                .thenReturn(false);
        Mockito.when(userService.getUserByUserId(anyString())).thenReturn(Mono.just(UserResponseModel.from(client1)));

        StepVerifier.create(eventService.updateEventDetails("1", requestModel))
                .expectError(MailSendException.class)
                .verify();

        verify(notificationService, times(1)).sendMail(anyString(), anyString(), anyString(), any(NotificationType.class));
    }

    @Test
    void whenGetEventsByInvalidClientId_thenReturnNotFound() {
        Mockito.when(eventRepository.findEventsByClientId(anyString()))
                .thenReturn(Flux.empty());
        StepVerifier.create(eventService.getEventsByClientId("invalid-client-id"))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void whenGetFilteredEvents_thenReturnAllEventsWithPrivacyFilter() {
        // Create test events with different privacy settings
        Event publicEvent = event1.toBuilder()
                .id("1")
                .eventPrivacy(EventPrivacy.PUBLIC)
                .build();

        Event privateEvent = event2.toBuilder()
                .id("2")
                .eventPrivacy(EventPrivacy.PRIVATE)
                .build();

        Mockito.when(eventRepository.findAll())
                .thenReturn(Flux.just(publicEvent, privateEvent));

        StepVerifier.create(eventService.getFilteredEvents())
                .expectNextMatches(dto ->
                        dto.getEventId().equals("1") &&
                                dto.getEventPrivacy().equals("PUBLIC") &&
                                dto.getEventAddress() != null &&
                                dto.getEventDateTime().equals(publicEvent.getEventDateTime().toString()) &&
                                dto.getEventType().equals(publicEvent.getEventType().toString())
                )
                .expectNextMatches(dto ->
                        dto.getEventId().equals("2") &&
                                dto.getEventPrivacy().equals("PRIVATE") &&
                                dto.getEventAddress() == null &&
                                dto.getEventDateTime().equals(privateEvent.getEventDateTime().toString()) &&
                                dto.getEventType().equals(privateEvent.getEventType().toString())
                )
                .verifyComplete();
    }

    @Test
    void whenGetFilteredEvents_PrivateEvent_thenReturnDTOWithNullAddress() {
        Address address = new Address("123 Private St", "SecretCity", "Hidden", "12345");
        Event privateEvent = event1.toBuilder()
                .id("1")
                .eventPrivacy(EventPrivacy.PRIVATE)
                .venue(address)
                .build();

        Mockito.when(eventRepository.findAll())
                .thenReturn(Flux.just(privateEvent));

        StepVerifier.create(eventService.getFilteredEvents())
                .expectNextMatches(dto ->
                        dto.getEventId().equals("1") &&
                                dto.getEventPrivacy().equals("PRIVATE") &&
                                dto.getEventAddress() == null &&  // Address should be null for private events
                                dto.getEventDateTime().equals(privateEvent.getEventDateTime().toString()) &&
                                dto.getEventType().equals(privateEvent.getEventType().toString())
                )
                .verifyComplete();
    }

    @Test
    void whenGetFilteredEvents_MultiplePrivacyTypes_thenReturnCorrectAddresses() {
        Event publicEvent1 = event1.toBuilder()
                .id("1")
                .eventPrivacy(EventPrivacy.PUBLIC)
                .build();

        Event privateEvent = event2.toBuilder()
                .id("2")
                .eventPrivacy(EventPrivacy.PRIVATE)
                .build();

        Event publicEvent2 = event1.toBuilder()
                .id("3")
                .eventPrivacy(EventPrivacy.PUBLIC)
                .build();

        Mockito.when(eventRepository.findAll())
                .thenReturn(Flux.just(publicEvent1, privateEvent, publicEvent2));

        StepVerifier.create(eventService.getFilteredEvents())
                .expectNextMatches(dto ->
                        dto.getEventId().equals("1") &&
                                dto.getEventPrivacy().equals("PUBLIC") &&
                                dto.getEventAddress() != null
                )
                .expectNextMatches(dto ->
                        dto.getEventId().equals("2") &&
                                dto.getEventPrivacy().equals("PRIVATE") &&
                                dto.getEventAddress() == null
                )
                .expectNextMatches(dto ->
                        dto.getEventId().equals("3") &&
                                dto.getEventPrivacy().equals("PUBLIC") &&
                                dto.getEventAddress() != null
                )
                .verifyComplete();
    }

    @Test
    void whenGetFilteredEvents_MixedAddressStates_thenHandleCorrectly() {
        Event publicWithAddress = event1.toBuilder()
                .id("1")
                .eventPrivacy(EventPrivacy.PUBLIC)
                .venue(new Address("123 Public St", "OpenCity", "Visible", "12345"))
                .build();

        Event publicWithoutAddress = event1.toBuilder()
                .id("2")
                .eventPrivacy(EventPrivacy.PUBLIC)
                .venue(null)
                .build();

        Event privateWithAddress = event2.toBuilder()
                .id("3")
                .eventPrivacy(EventPrivacy.PRIVATE)
                .venue(new Address("123 Private St", "SecretCity", "Hidden", "67890"))
                .build();

        Mockito.when(eventRepository.findAll())
                .thenReturn(Flux.just(publicWithAddress, publicWithoutAddress, privateWithAddress));

        StepVerifier.create(eventService.getFilteredEvents())
                .expectNextMatches(dto ->
                        dto.getEventId().equals("1") &&
                                dto.getEventPrivacy().equals("PUBLIC") &&
                                dto.getEventAddress() != null &&
                                dto.getEventAddress().getStreetAddress().equals("123 Public St")
                )
                .expectNextMatches(dto ->
                        dto.getEventId().equals("2") &&
                                dto.getEventPrivacy().equals("PUBLIC") &&
                                dto.getEventAddress() == null
                )
                .expectNextMatches(dto ->
                        dto.getEventId().equals("3") &&
                                dto.getEventPrivacy().equals("PRIVATE") &&
                                dto.getEventAddress() == null
                )
                .verifyComplete();
    }

    @Test
    void whenGetFilteredEvents_NoEvents_thenReturnEmptyFlux() {
        Mockito.when(eventRepository.findAll())
                .thenReturn(Flux.empty());

        StepVerifier.create(eventService.getFilteredEvents())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void whenGetFilteredEvents_WithNullAddress_thenReturnDTOWithNullAddress() {
        Event eventWithNullAddress = event1.toBuilder()
                .id("1")
                .eventPrivacy(EventPrivacy.PUBLIC)
                .venue(null)
                .build();

        Mockito.when(eventRepository.findAll())
                .thenReturn(Flux.just(eventWithNullAddress));

        StepVerifier.create(eventService.getFilteredEvents())
                .expectNextMatches(dto ->
                        dto.getEventId().equals("1") &&
                                dto.getEventPrivacy().equals("PUBLIC") &&
                                dto.getEventAddress() == null
                )
                .verifyComplete();
    }
}