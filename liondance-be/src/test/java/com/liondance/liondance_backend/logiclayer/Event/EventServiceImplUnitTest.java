package com.liondance.liondance_backend.logiclayer.Event;

import com.liondance.liondance_backend.datalayer.Event.*;
import com.liondance.liondance_backend.datalayer.Feedback.Feedback;
import com.liondance.liondance_backend.datalayer.Feedback.FeedbackRepository;
import com.liondance.liondance_backend.datalayer.Notification.NotificationType;
import com.liondance.liondance_backend.datalayer.User.Client;
import com.liondance.liondance_backend.datalayer.User.Role;
import com.liondance.liondance_backend.datalayer.User.User;
import com.liondance.liondance_backend.datalayer.User.UserRepository;
import com.liondance.liondance_backend.datalayer.common.Address;
import com.liondance.liondance_backend.logiclayer.Notification.NotificationService;
import com.liondance.liondance_backend.logiclayer.User.UserService;
import com.liondance.liondance_backend.presentationlayer.Event.EventRequestModel;
import com.liondance.liondance_backend.presentationlayer.Event.EventResponseModel;
import com.liondance.liondance_backend.presentationlayer.Feedback.FeedbackRequestModel;
import com.liondance.liondance_backend.presentationlayer.User.UserResponseModel;
import com.liondance.liondance_backend.utils.DataLoaderService;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.List;

import static com.liondance.liondance_backend.datalayer.Event.EventStatus.PENDING;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplUnitTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private UserRepository userRepository;

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

    private User performer1;
    private User performer2;

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

        performer1 = Client.builder()
                .userId(UUID.randomUUID().toString())
                .firstName("JohnPerforms")
                .lastName("AndDoesntHack")
                .email("liondance@yopmail.com")
                .phone("1234567890")
                .address(
                        new Address(
                                "1234 Main St.",
                                "Springfield",
                                "Quebec",
                                "J2X 2J4")
                )
                .roles(EnumSet.of(Role.STAFF))
                .associatedId("johntheperformer")
                .build();

        performer2 = Client.builder()
                .userId(UUID.randomUUID().toString())
                .firstName("JanePerforms")
                .lastName("DoesMoves")
                .email("liondance@yopmail.com")
                .phone("1234567890")
                .address(
                        new Address(
                                "1234 Main St.",
                                "Springfield",
                                "Quebec",
                                "J2X 2J4")
                )
                .roles(EnumSet.of(Role.STAFF))
                .associatedId("janetheperforma")
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
                .eventDateTime(LocalDate.now().atTime(LocalTime.NOON).toInstant(ZoneOffset.UTC))
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
                .eventDateTime(LocalDate.now().atTime(LocalTime.NOON).toInstant(ZoneOffset.UTC))
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
                .eventDateTime(LocalDate.now().atTime(LocalTime.NOON).toInstant(ZoneOffset.UTC))
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
                .expectNextCount(1)
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
                .eventDateTime(LocalDate.now().atTime(LocalTime.NOON).toInstant(ZoneOffset.UTC))
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
                .eventDateTime(LocalDate.now().atTime(LocalTime.NOON).toInstant(ZoneOffset.UTC))
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
                .eventDateTime(LocalDate.now().atTime(LocalTime.NOON).toInstant(ZoneOffset.UTC))
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
        Event publicEvent = event1.toBuilder()
                .eventId("1")
                .eventPrivacy(EventPrivacy.PUBLIC)
                .build();

        Event privateEvent = event2.toBuilder()
                .eventId("2")
                .eventPrivacy(EventPrivacy.PRIVATE)
                .build();

        Mockito.when(eventRepository.findAll())
                .thenReturn(Flux.just(publicEvent, privateEvent));

        StepVerifier.create(eventService.getFilteredEvents())
                .expectNextMatches(dto -> {
                    assertEquals(publicEvent.getEventId(), dto.getEventId());
                    assertEquals(publicEvent.getEventPrivacy(), dto.getEventPrivacy());
                    assertNotNull(dto.getVenue());
                    assertEquals(publicEvent.getEventDateTime(), dto.getEventDateTime());
                    assertEquals(publicEvent.getEventType(), dto.getEventType());
                    return true;
                })
                .expectNextMatches(dto -> {
                    assertEquals(privateEvent.getEventId(), dto.getEventId());
                    assertEquals(privateEvent.getEventPrivacy(), dto.getEventPrivacy());
                    assertNull(dto.getVenue());
                    assertEquals(privateEvent.getEventDateTime(), dto.getEventDateTime());
                    assertEquals(privateEvent.getEventType(), dto.getEventType());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void whenGetFilteredEvents_PrivateEvent_thenReturnDTOWithNullAddress() {
        Address address = new Address("123 Private St", "SecretCity", "Hidden", "12345");
        Event privateEvent = event1.toBuilder()
                .eventId("1")
                .eventPrivacy(EventPrivacy.PRIVATE)
                .venue(address)
                .build();

        Mockito.when(eventRepository.findAll())
                .thenReturn(Flux.just(privateEvent));

        StepVerifier.create(eventService.getFilteredEvents())
                .expectNextMatches(dto -> {
                    assertEquals(privateEvent.getEventId(), dto.getEventId());
                    assertEquals(privateEvent.getEventPrivacy(), dto.getEventPrivacy());
                    assertNull(dto.getVenue());
                    assertEquals(privateEvent.getEventDateTime(), dto.getEventDateTime());
                    assertEquals(privateEvent.getEventType(), dto.getEventType());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void whenGetFilteredEvents_MultiplePrivacyTypes_thenReturnCorrectAddresses() {
        Event publicEvent1 = event1.toBuilder()
                .eventId("1")
                .eventPrivacy(EventPrivacy.PUBLIC)
                .build();

        Event privateEvent = event2.toBuilder()
                .eventId("2")
                .eventPrivacy(EventPrivacy.PRIVATE)
                .build();

        Event publicEvent2 = event1.toBuilder()
                .eventId("3")
                .eventPrivacy(EventPrivacy.PUBLIC)
                .build();

        Mockito.when(eventRepository.findAll())
                .thenReturn(Flux.just(publicEvent1, privateEvent, publicEvent2));

        StepVerifier.create(eventService.getFilteredEvents())
                .expectNextMatches(dto -> {
                    assertEquals(publicEvent1.getEventId(), dto.getEventId());
                    assertEquals(publicEvent1.getEventPrivacy(), dto.getEventPrivacy());
                    assertNotNull(dto.getVenue());
                    return true;
                })
                .expectNextMatches(dto -> {
                    assertEquals(privateEvent.getEventId(), dto.getEventId());
                    assertEquals(privateEvent.getEventPrivacy(), dto.getEventPrivacy());
                    assertNull(dto.getVenue());
                    return true;
                })
                .expectNextMatches(dto -> {
                    assertEquals(publicEvent2.getEventId(), dto.getEventId());
                    assertEquals(publicEvent2.getEventPrivacy(), dto.getEventPrivacy());
                    assertNotNull(dto.getVenue());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void whenGetFilteredEvents_MixedAddressStates_thenHandleCorrectly() {
        Event publicWithAddress = event1.toBuilder()
                .eventId("1")
                .eventPrivacy(EventPrivacy.PUBLIC)
                .venue(new Address("123 Public St", "OpenCity", "Visible", "12345"))
                .build();

        Event publicWithoutAddress = event1.toBuilder()
                .eventId("2")
                .eventPrivacy(EventPrivacy.PUBLIC)
                .venue(null)
                .build();

        Event privateWithAddress = event2.toBuilder()
                .eventId("3")
                .eventPrivacy(EventPrivacy.PRIVATE)
                .venue(new Address("123 Private St", "SecretCity", "Hidden", "67890"))
                .build();

        Mockito.when(eventRepository.findAll())
                .thenReturn(Flux.just(publicWithAddress, publicWithoutAddress, privateWithAddress));

        StepVerifier.create(eventService.getFilteredEvents())
                .expectNextMatches(dto -> {
                    assertEquals(publicWithAddress.getEventId(), dto.getEventId());
                    assertEquals(publicWithAddress.getEventPrivacy(), dto.getEventPrivacy());
                    assertNotNull(dto.getVenue());
                    assertEquals(publicWithAddress.getVenue().getStreetAddress(), dto.getVenue().getStreetAddress());
                    return true;
                })
                .expectNextMatches(dto -> {
                    assertEquals(publicWithoutAddress.getEventId(), dto.getEventId());
                    assertEquals(publicWithoutAddress.getEventPrivacy(), dto.getEventPrivacy());
                    assertNull(dto.getVenue());
                    return true;
                })
                .expectNextMatches(dto -> {
                    assertEquals(privateWithAddress.getEventId(), dto.getEventId());
                    assertEquals(privateWithAddress.getEventPrivacy(), dto.getEventPrivacy());
                    assertNull(dto.getVenue());
                    return true;
                })
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
                .eventId("1")
                .eventPrivacy(EventPrivacy.PUBLIC)
                .venue(null)
                .build();

        Mockito.when(eventRepository.findAll())
                .thenReturn(Flux.just(eventWithNullAddress));

        StepVerifier.create(eventService.getFilteredEvents())
                .expectNextMatches(dto -> {
                    assertEquals(eventWithNullAddress.getEventId(), dto.getEventId());
                    assertEquals(eventWithNullAddress.getEventPrivacy(), dto.getEventPrivacy());
                    assertNull(dto.getVenue());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void submitFeedback_withValidRequest_shouldSaveFeedback() {
        FeedbackRequestModel feedbackRequestModel = FeedbackRequestModel.builder()
                .feedback("Great event!")
                .rating(5)
                .build();

        event1.setEventStatus(EventStatus.COMPLETED); // Set event status to COMPLETED

        Mockito.when(eventRepository.findEventByEventId(event1.getEventId()))
                .thenReturn(Mono.just(event1));
        Mockito.when(feedbackRepository.save(any(Feedback.class)))
                .thenReturn(Mono.just(Feedback.builder()
                        .feedbackId(UUID.randomUUID().toString())
                        .timestamp(Instant.now())
                        .feedback(feedbackRequestModel.getFeedback())
                        .rating(feedbackRequestModel.getRating())
                        .eventId(event1.getEventId())
                        .build()));

        StepVerifier.create(eventService.submitFeedback(event1.getEventId(), Mono.just(feedbackRequestModel), client1))
                .expectNextMatches(response -> response.getFeedback().equals("Great event!") && response.getRating() == 5)
                .verifyComplete();
    }

    @Test
    void submitFeedback_withNonExistentEvent_shouldReturnNotFound() {
        FeedbackRequestModel feedbackRequestModel = FeedbackRequestModel.builder()
                .feedback("Great event!")
                .rating(5)
                .build();

        Mockito.when(eventRepository.findEventByEventId("nonexistent-event-id"))
                .thenReturn(Mono.empty());

        StepVerifier.create(eventService.submitFeedback("nonexistent-event-id", Mono.just(feedbackRequestModel), client1))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void submitFeedback_withIncompleteEvent_shouldReturnIllegalState() {
        event1.setEventStatus(EventStatus.PENDING);

        FeedbackRequestModel feedbackRequestModel = FeedbackRequestModel.builder()
                .feedback("Great event!")
                .rating(5)
                .build();

        Mockito.when(eventRepository.findEventByEventId(event1.getEventId()))
                .thenReturn(Mono.just(event1));

        StepVerifier.create(eventService.submitFeedback(event1.getEventId(), Mono.just(feedbackRequestModel), client1))
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void requestFeedback_withValidEventId_shouldSendEmail() {
        String frontendUrl = "http://localhost:5173";
        System.setProperty("FRONTEND_URL", frontendUrl);

        Mockito.when(eventRepository.findEventByEventId(event1.getEventId()))
                .thenReturn(Mono.just(event1));
        Mockito.when(userRepository.findUserByUserId(event1.getClientId()))
                .thenReturn(Mono.just(client1));
        Mockito.when(notificationService.sendMail(anyString(), anyString(), anyString(), any(NotificationType.class)))
                .thenReturn(true);

        StepVerifier.create(eventService.requestFeedback(event1.getEventId()))
                .verifyComplete();
    }

    @Test
    void requestFeedback_withFailedEmailSend_shouldReturnMailSendException() {
        String frontendUrl = "http://localhost:5173";
        System.setProperty("FRONTEND_URL", frontendUrl);

        Mockito.when(eventRepository.findEventByEventId(event1.getEventId()))
                .thenReturn(Mono.just(event1));
        Mockito.when(userRepository.findUserByUserId(event1.getClientId()))
                .thenReturn(Mono.just(client1));
        Mockito.when(notificationService.sendMail(anyString(), anyString(), anyString(), any(NotificationType.class)))
                .thenReturn(false);

        StepVerifier.create(eventService.requestFeedback(event1.getEventId()))
                .expectError(MailSendException.class)
                .verify();
    }

    @Test
    void requestFeedback_withNonExistentUser_shouldReturnNotFound() {
        Mockito.when(eventRepository.findEventByEventId(event1.getEventId()))
                .thenReturn(Mono.just(event1));
        Mockito.when(userRepository.findUserByUserId(event1.getClientId()))
                .thenReturn(Mono.empty());

        StepVerifier.create(eventService.requestFeedback(event1.getEventId()))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void requestFeedback_withNonExistentEventId_shouldReturnNotFound() {
        Mockito.when(eventRepository.findEventByEventId("nonexistent-event-id"))
                .thenReturn(Mono.empty());

        StepVerifier.create(eventService.requestFeedback("nonexistent-event-id"))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void assignPerformersWithValidEventAndPerformers() {
        List<String> performerIds = List.of("performer1", "performer2");
        event1.setPerformers(new HashMap<>());
        Mockito.when(eventRepository.findEventByEventId(anyString()))
                .thenReturn(Mono.just(event1));
        Mockito.when(userRepository.findUserByUserId(anyString()))
                .thenReturn(Mono.just(client1));
        Mockito.when(eventRepository.save(any(Event.class)))
                .thenReturn(Mono.just(event1));
        Mockito.when(notificationService.sendMail(anyString(), anyString(), anyString(), any(NotificationType.class), anyString()))
                .thenReturn(true);

        StepVerifier.create(eventService.assignPerformers(event1.getEventId(), performerIds))
                .expectNextMatches(eventResponseModel -> EventResponseModel.from(event1).equals(eventResponseModel))
                .verifyComplete();
    }

    @Test
    void assignPerformersWithNonExistentEvent() {
        List<String> performerIds = List.of("performer1", "performer2");
        Mockito.when(eventRepository.findEventByEventId(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(eventService.assignPerformers("nonexistent-event-id", performerIds))
                .expectError(NotFoundException.class)
                .verify();
    }
    @Test

    void assignPerformersWithFailedEmailSend_shouldThrowMailSendException() {
        List<String> performerIds = List.of("performer1", "performer2");
        event1.setPerformers(new HashMap<>());
        Mockito.when(eventRepository.findEventByEventId(anyString()))
                .thenReturn(Mono.just(event1));
        Mockito.when(userRepository.findUserByUserId(anyString()))
                .thenReturn(Mono.just(client1));
        Mockito.when(notificationService.sendMail(anyString(), anyString(), anyString(), any(NotificationType.class), anyString()))
                .thenReturn(false);

        StepVerifier.create(eventService.assignPerformers(event1.getEventId(), performerIds))
                .expectError(MailSendException.class)
                .verify();
    }

    @Test
    void removePerformersWithValidEventAndPerformers() {
        List<String> performerIds = List.of("performer1", "performer2");
        event1.setPerformers(DataLoaderService.createPerformersMap("performer1", "performer2"));
        Mockito.when(eventRepository.findEventByEventId(anyString()))
                .thenReturn(Mono.just(event1));
        Mockito.when(userRepository.findUserByUserId(anyString()))
                .thenReturn(Mono.just(client1));
        Mockito.when(eventRepository.save(any(Event.class)))
                .thenReturn(Mono.just(event1));
        Mockito.when(notificationService.sendMail(anyString(), anyString(), anyString(), any(NotificationType.class)))
                .thenReturn(true);

        StepVerifier.create(eventService.removePerformers(event1.getEventId(), performerIds))
                .expectNextMatches(eventResponseModel -> EventResponseModel.from(event1).equals(eventResponseModel))
                .verifyComplete();
    }

    @Test
    void removePerformersWithNonExistentEvent() {
        List<String> performerIds = List.of("performer1", "performer2");
        Mockito.when(eventRepository.findEventByEventId(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(eventService.removePerformers("nonexistent-event-id", performerIds))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void removePerformersWithFailedEmailSend_shouldThrowMailSendException() {
        List<String> performerIds = List.of("performer1", "performer2");
        event1.setPerformers(DataLoaderService.createPerformersMap("performer1", "performer2"));
        Mockito.when(eventRepository.findEventByEventId(anyString()))
                .thenReturn(Mono.just(event1));
        Mockito.when(userRepository.findUserByUserId(anyString()))
                .thenReturn(Mono.just(client1));
        Mockito.when(notificationService.sendMail(anyString(), anyString(), anyString(), any(NotificationType.class)))
                .thenReturn(false);

        StepVerifier.create(eventService.removePerformers(event1.getEventId(), performerIds))
                .expectError(MailSendException.class)
                .verify();
    }

    @Test
    void whenGetPerformers_withValidEventId_thenReturnPerformersList() {
        event1.setPerformers(DataLoaderService.createPerformersMap(performer1.getUserId(), performer2.getUserId()));

        Mockito.when(eventRepository.findEventByEventId(event1.getEventId()))
                .thenReturn(Mono.just(event1));
        Mockito.when(userService.getUserByUserId(performer1.getUserId()))
                        .thenReturn(Mono.just(performer1).map(UserResponseModel::from));
        Mockito.when(userService.getUserByUserId(performer2.getUserId()))
                .thenReturn(Mono.just(performer2).map(UserResponseModel::from));

        StepVerifier.create(eventService.getPerformers(event1.getEventId()))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void whenGetPerformers_withNonExistentEventId_thenThrowNotFoundException() {
        String nonExistentEventId = UUID.randomUUID().toString();

        Mockito.when(eventRepository.findEventByEventId(nonExistentEventId))
                .thenReturn(Mono.empty());

        StepVerifier.create(eventService.getPerformers(nonExistentEventId))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void whenGetPerformers_withEmptyPerformersList_thenReturnEmptyList() {
        event1.setPerformers(new HashMap<>());

        Mockito.when(eventRepository.findEventByEventId(event1.getEventId()))
                .thenReturn(Mono.just(event1));

        StepVerifier.create(eventService.getPerformers(event1.getEventId()))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void whenGetPerformers_withNullPerformersList_thenReturnEmptyList() {
        event1.setPerformers(null);

        Mockito.when(eventRepository.findEventByEventId(event1.getEventId()))
                .thenReturn(Mono.just(event1));

        StepVerifier.create(eventService.getPerformers(event1.getEventId()))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void whenUpdateEventStatus_FailedToSendEmail_thenThrowMailSendException() {
        UserResponseModel userResponse = UserResponseModel.from(client1);

        Mockito.when(eventRepository.findEventByEventId(anyString()))
                .thenReturn(Mono.just(event1));
        Mockito.when(userService.getUserByUserId(anyString()))
                .thenReturn(Mono.just(userResponse));
        Mockito.when(notificationService.sendMail(
                        eq(client1.getEmail()),
                        eq("LVH Lion Dance Team - Event Status Update"),
                        anyString(),
                        eq(NotificationType.EVENT_STATUS_UPDATE)))
                .thenReturn(false);

        StepVerifier.create(eventService.updateEventStatus(event1.getEventId(), Mono.just(EventStatus.CONFIRMED)))
                .expectErrorMatches(throwable ->
                        throwable instanceof MailSendException &&
                                throwable.getMessage().equals("Failed to send email to " + client1.getEmail()))
                .verify();

        verify(notificationService, times(1)).sendMail(
                anyString(),
                anyString(),
                anyString(),
                eq(NotificationType.EVENT_STATUS_UPDATE)
        );
    }

}