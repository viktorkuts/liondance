
package com.liondance.liondance_backend.presentationlayer.Event;

import com.liondance.liondance_backend.datalayer.Event.*;
import com.liondance.liondance_backend.datalayer.User.Client;
import com.liondance.liondance_backend.datalayer.User.Role;
import com.liondance.liondance_backend.datalayer.User.User;
import com.liondance.liondance_backend.datalayer.User.UserRepository;
import com.liondance.liondance_backend.datalayer.common.Address;
import com.liondance.liondance_backend.utils.WebTestAuthConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClientConfigurer;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.EnumSet;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.data.mongodb.port= 0"})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
class EventControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private EventRepository eventRepository;

    User staff = Client.builder()
            .userId(UUID.randomUUID().toString())
            .firstName("JaneStaff")
            .lastName("DoeStaff")
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
            .associatedId("thetesterstaff")
            .build();

    Client client1 = Client.builder()
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
                .associatedId("thetester1")
            .build();
    Client client2 = Client.builder()
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
            .associatedId("thetester2")
            .build();

    Event event1 = Event.builder()
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

    Event event2 = Event.builder()
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

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setupDB() {
        StepVerifier.create(eventRepository.deleteAll())
                .verifyComplete();

        StepVerifier.create(userRepository.deleteAll())
                .verifyComplete();

        Publisher<Event> eventPublisher = Flux.just(event1, event2)
                .flatMap(eventRepository::save);

        Publisher<User> userPublisher = Flux.just(client1, client2)
                .flatMap(userRepository::save);

        StepVerifier.create(eventPublisher)
                .expectNextCount(2)
                .verifyComplete();

        StepVerifier.create(userPublisher)
                .expectNextCount(2)
                .verifyComplete();
    }

    // Test for getAllEvents
    @Test
    void whenGetAllEvents_thenReturnAllEvents() {
        webTestClient.get().uri("/api/v1/events")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EventResponseModel.class)
                .hasSize(2)
                .value(events -> {
                    assertThat(events).usingElementComparatorIgnoringFields("eventDateTime")
                            .contains(EventResponseModel.from(event1), EventResponseModel.from(event2));
                });
    }

    @Test
    void whenGetAllEvents_IncorrectEndpoint_thenReturn404() {
        webTestClient.get().uri("/event")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenBookEvent_thenReturnEventResponseModel() {
        StepVerifier.create(eventRepository.findAll())
                .expectNextCount(2)
                .verifyComplete();

        EventRequestModel eventRequestModel = EventRequestModel.builder()
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
                .eventPrivacy(EventPrivacy.PRIVATE)
                .specialRequest("Special request")
                .build();

        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(client1))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .post()
                .uri("/api/v1/events")
                .bodyValue(eventRequestModel)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(EventResponseModel.class);

        StepVerifier.create(eventRepository.findAll())
                .expectNextCount(3)
                .verifyComplete();

    }

    @Test
    void whenBookEvent_IncorrectEndpoint_thenReturn404() {
        EventRequestModel eventRequestModel = EventRequestModel.builder()
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

        webTestClient.post()
                .uri("/api/v1/event")
                .bodyValue(eventRequestModel)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenBookEvent_InvalidRequest_thenReturnUnprocessableEntityStatus() {
        EventRequestModel requestModel = EventRequestModel.builder().build();

        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(client1))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .post()
                .uri("/api/v1/events")
                .bodyValue(requestModel)
                .exchange()
                .expectStatus().isEqualTo(422);
    }

    @Test
    void whenBookEvent_EmptyRequest_thenReturnBadRequestStatus() {
        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(client1))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .post()
                .uri("/api/v1/events")
                .bodyValue(new EventRequestModel())
                .exchange()
                .expectStatus().isEqualTo(422);
    }

    @Test
    void whenUpdateEventStatus_thenReturnEventResponseModel() {
        Event event = eventRepository.findAll().blockFirst();
        String eventId = event.getEventId();

        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .patch()
                .uri("/api/v1/events/" + eventId + "/status")
                .header("Content-Type", "application/json")
                .bodyValue("{\"eventStatus\": \"CONFIRMED\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody(EventResponseModel.class);
    }

    @Test
    void whenUpdateEventStatus_IncorrectEndpoint_thenReturn404() {
        Event event = eventRepository.findAll().blockFirst();
        String eventId = event.getEventId();

        webTestClient.patch()
                .uri("/api/v1/event/" + eventId + "/status")
                .header("Content-Type", "application/json")
                .bodyValue("{\"eventStatus\": \"CONFIRMED\"}")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenUpdateEventStatus_withValidStatus_thenReturnEventResponseModel() {
        Event event = eventRepository.findAll().blockFirst();
        String eventId = event.getEventId();

        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .patch()
                .uri("/api/v1/events/" + eventId + "/status")
                .header("Content-Type", "application/json")
                .bodyValue("{\"eventStatus\": \"CONFIRMED\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody(EventResponseModel.class);
    }

    @Test
    void whenUpdateEventStatus_withNullStatus_thenReturnServerError() {
        Event event = eventRepository.findAll().blockFirst();
        String eventId = event.getEventId();

        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .patch()
                .uri("/api/v1/events/" + eventId + "/status")
                .header("Content-Type", "application/json")
                .bodyValue("{\"eventStatus\": null}")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void whenUpdateEventStatus_withEmptyStatus_thenReturnServerError() {
        Event event = eventRepository.findAll().blockFirst();
        String eventId = event.getEventId();

        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .patch()
                .uri("/api/v1/events/" + eventId + "/status")
                .header("Content-Type", "application/json")
                .bodyValue("{\"eventStatus\": \"\"}")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void whenUpdateEventStatus_withInvalidStatus_thenReturnServerError() {
        Event event = eventRepository.findAll().blockFirst();
        String eventId = event.getEventId();

        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .patch()
                .uri("/api/v1/events/" + eventId + "/status")
                .header("Content-Type", "application/json")
                .bodyValue("{\"eventStatus\": \"INVALID_STATUS\"}")
                .exchange()
                .expectStatus().is5xxServerError();
    }


    @Test
    void whenGetEventById_thenReturnEventResponseModel() {
        Event event = eventRepository.findAll().blockFirst();
        String eventId = event.getEventId();

        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .get()
                .uri("/api/v1/events/" + eventId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EventResponseModel.class);
    }

    @Test
    void whenGetEventById_IncorrectEndpoint_thenReturn404() {
        Event event = eventRepository.findAll().blockFirst();
        String eventId = event.getEventId();

        webTestClient.get()
                .uri("/api/v1/event/" + eventId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenRescheduleEvent_thenReturnEventResponseModel() {
        Event event = eventRepository.findAll().blockFirst();
        String eventId = event.getEventId();

        webTestClient.patch()
                .uri("/api/v1/events/" + eventId + "/date")
                .header("Content-Type", "application/json")
                .bodyValue("{\"eventDateTime\": \"2021-12-31T23:59:59Z\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody(EventResponseModel.class);
    }

    @Test
    void whenRescheduleEvent_IncorrectEndpoint_thenReturn404() {
        Event event = eventRepository.findAll().blockFirst();
        String eventId = event.getEventId();

        webTestClient.patch()
                .uri("/api/v1/event/" + eventId + "/date")
                .header("Content-Type", "application/json")
                .bodyValue("{\"eventDateTime\": \"2021-12-31T23:59:59Z\"}")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenUpdateEventDetails_thenReturnEventResponseModel() {
        Event event = eventRepository.findAll().blockFirst();
        String eventId = event.getEventId();

        EventRequestModel eventRequestModel = EventRequestModel.builder()
                .venue(new Address("1234 Main St.", "Springfield", "Quebec", "J2X 2J4"))
                .eventDateTime(LocalDate.now().atTime(LocalTime.NOON).toInstant(ZoneOffset.UTC))
                .eventType(EventType.WEDDING)
                .paymentMethod(PaymentMethod.CASH)
                .eventPrivacy(EventPrivacy.PRIVATE)
                .specialRequest("Special request")
                .build();

        webTestClient.put()
                .uri("/api/v1/events/" + eventId)
                .bodyValue(eventRequestModel)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EventResponseModel.class)
                .value(response -> {
                    assertThat(response.getEventPrivacy()).isEqualTo(eventRequestModel.getEventPrivacy());
                    assertThat(response.getVenue()).isEqualTo(eventRequestModel.getVenue());
                    assertThat(response.getEventDateTime()).isEqualTo(eventRequestModel.getEventDateTime());
                });
    }

    @Test
    void whenUpdateEventDetails_IncorrectEndpoint_thenReturn404() {
        Event event = eventRepository.findAll().blockFirst();
        String eventId = event.getEventId();

        EventRequestModel eventRequestModel = EventRequestModel.builder()
                .venue(new Address("1234 Main St.", "Springfield", "Quebec", "J2X 2J4"))
                .eventDateTime(LocalDate.now().atTime(LocalTime.NOON).toInstant(ZoneOffset.UTC))
                .eventType(EventType.WEDDING)
                .paymentMethod(PaymentMethod.CASH)
                .specialRequest("Special request")
                .build();

        webTestClient.put()
                .uri("/api/v1/event/" + eventId)
                .bodyValue(eventRequestModel)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenGetFilteredEvents_thenReturnFilteredEvents() {
        // Create test events with different privacy settings
        Event publicEvent = Event.builder()
                .venue(
                        new Address(
                                "1234 Main St.",
                                "Springfield",
                                "Quebec",
                                "J2X 2J4")
                )
                .eventDateTime(Instant.now())
                .eventType(EventType.WEDDING)
                .eventPrivacy(EventPrivacy.PUBLIC)
                .paymentMethod(PaymentMethod.CASH)
                .specialRequest("Public event")
                .build();

        Event privateEvent = Event.builder()
                .venue(
                        new Address(
                                "5678 Side St.",
                                "Montreal",
                                "Quebec",
                                "H2X 1Y2")
                )
                .eventDateTime(Instant.now())
                .eventType(EventType.BIRTHDAY)
                .eventPrivacy(EventPrivacy.PRIVATE)
                .paymentMethod(PaymentMethod.CASH)
                .specialRequest("Private event")
                .build();

        // Save test events
        StepVerifier.create(eventRepository.deleteAll())
                .verifyComplete();

        Publisher<Event> eventPublisher = Flux.just(publicEvent, privateEvent)
                .flatMap(eventRepository::save);

        StepVerifier.create(eventPublisher)
                .expectNextCount(2)
                .verifyComplete();

        // Test the endpoint
        webTestClient.get().uri("/api/v1/events/filtered-events")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EventDisplayDTO.class)
                .value(events -> {
                    assertThat(events).hasSize(2);
                    events.forEach(dto -> {
                        if (dto.getEventPrivacy().equals(EventPrivacy.PUBLIC.toString())) {
                            assertThat(dto.getEventAddress()).isNotNull();
                        } else {
                            assertThat(dto.getEventAddress()).isNull();
                        }
                    });
                });
    }

    @Test
    void whenGetFilteredEvents_IncorrectEndpoint_thenReturn404() {
        webTestClient.get().uri("/api/v1/filtered-event")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenGetFilteredEvents_EmptyDatabase_thenReturnEmptyList() {
        // Clear the database
        StepVerifier.create(eventRepository.deleteAll())
                .verifyComplete();

        // Test the endpoint
        webTestClient.get().uri("/api/v1/events/filtered-events")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EventDisplayDTO.class)
                .hasSize(0);
    }

//    @Test
//    void whenGetFilteredEvents_VerifyDTOFields() {
//        Event testEvent = Event.builder()
//                .venue(
//                        new Address(
//                                "1234 Test St.",
//                                "TestCity",
//                                "Quebec",
//                                "J2X 2J4")
//                )
//                .eventDateTime(Instant.parse("2024-12-31T23:59:59Z"))
//                .eventType(EventType.WEDDING)
//                .eventPrivacy(EventPrivacy.PUBLIC)
//                .paymentMethod(PaymentMethod.CASH)
//                .specialRequest("Test event")
//                .build();
//
//        // Save test event
//        StepVerifier.create(eventRepository.deleteAll())
//                .verifyComplete();
//
//        StepVerifier.create(eventRepository.save(testEvent))
//                .expectNextCount(1)
//                .verifyComplete();
//
//        // Test the endpoint
//        webTestClient.get().uri("/api/v1/events/filtered-events")
//                .exchange()
//                .expectStatus().isOk()
//                .expectBodyList(EventDisplayDTO.class)
//                .value(events -> {
//                    assertThat(events).hasSize(1);
//                    EventDisplayDTO dto = events.get(0);
//                    assertThat(dto.getEventId()).isNotNull();
//                    assertThat(dto.getEventDateTime()).contains("2024-12-31T23:59:59Z");
//                    assertThat(dto.getEventType()).isEqualTo(EventType.WEDDING.toString());
//                    assertThat(dto.getEventPrivacy()).isEqualTo(EventPrivacy.PUBLIC.toString());
//                    assertThat(dto.getEventAddress()).isNotNull();
//                    assertThat(dto.getEventAddress().getCity()).isEqualTo("TestCity");
//                });
//    }

    @Test
    void whenUpdateEventDetails_InvalidRequest_thenReturnUnprocessableEntityStatus() {
        Event event = eventRepository.findAll().blockFirst();
        String eventId = event.getEventId();

        EventRequestModel eventRequestModel = EventRequestModel.builder().build();

        webTestClient.put()
                .uri("/api/v1/events/" + eventId)
                .bodyValue(eventRequestModel)
                .exchange()
                .expectStatus().isEqualTo(422);
    }

}

