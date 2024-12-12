package com.liondance.liondance_backend.presentationlayer.Event;

import com.liondance.liondance_backend.datalayer.Event.Event;
import com.liondance.liondance_backend.datalayer.Event.EventRepository;
import com.liondance.liondance_backend.datalayer.Event.EventType;
import com.liondance.liondance_backend.datalayer.Event.PaymentMethod;
import com.liondance.liondance_backend.datalayer.common.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.data.mongodb.port= 0", "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration"})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
class EventControllerIntegrationTest {

//    @Autowired
//    private WebTestClient webTestClient;
//
//    @Autowired
//    private EventRepository eventRepository;
//
//
//    Event event1 = Event.builder()
//            .firstName("Jane")
//                .lastName("Doe")
//                .email("liondance@yopmail.com")
//                .phone("1234567890")
//                .address(
//                        new Address(
//            "1234 Main St.",
//                                "Springfield",
//                                "Quebec",
//                                "J2X 2J4")
//                )
//                        .eventDateTime(LocalDate.now().atTime(LocalTime.NOON))
//            .eventType(EventType.WEDDING)
//                .paymentMethod(PaymentMethod.CASH)
//                .specialRequest("Special request")
//                .build();
//
//    Event event2 = Event.builder()
//            .firstName("Robert")
//            .middleName("John")
//            .lastName("DeNiro")
//            .email("liondance@yopmail.com")
//            .phone("1234567890")
//            .address(
//                    new Address(
//                            "1234 Main St.",
//                            "Springfield",
//                            "Quebec",
//                            "J2X 2J4")
//            )
//            .eventDateTime(LocalDate.now().atTime(LocalTime.NOON))
//            .eventType(EventType.WEDDING)
//            .paymentMethod(PaymentMethod.CASH)
//            .specialRequest("Special request")
//            .build();
//
//    @BeforeEach
//    public void setupDB() {
//        StepVerifier.create(eventRepository.deleteAll())
//                .verifyComplete();
//
//        Publisher<Event> eventPublisher = eventRepository.save(event1)
//                .then(eventRepository.save(event2));
//
//        StepVerifier.create(eventPublisher)
//                .expectNextCount(2)
//                .verifyComplete();
//    }
//
//    // Test for getAllEvents
//    @Test
//    void whenGetAllEvents_thenReturnAllEvents() {
//        webTestClient.get().uri("/events")
//                .exchange()
//                .expectStatus().isOk()
//                .expectBodyList(EventResponseModel.class)
//                .hasSize(2)
//                .contains(EventResponseModel.from(event1), EventResponseModel.from(event2));
//    }
//
//    @Test
//    void whenGetAllEvents_IncorrectEndpoint_thenReturn404() {
//        webTestClient.get().uri("/event")
//                .exchange()
//                .expectStatus().isNotFound();
//    }
//
//    @Test
//    void whenBookEvent_thenReturnEventResponseModel() {
//        StepVerifier.create(eventRepository.findAll())
//                .expectNextCount(2)
//                .verifyComplete();
//
//        EventRequestModel eventRequestModel = EventRequestModel.builder()
//                .firstName("Jane")
//                .lastName("Doe")
//                .email("liondance@yopmail.com")
//                .phone("1234567890")
//                .address(
//                        new Address(
//                                "1234 Main St.",
//                                "Springfield",
//                                "Quebec",
//                                "J2X 2J4")
//                )
//                .eventDateTime(LocalDate.now().atTime(LocalTime.NOON))
//                .eventType(EventType.WEDDING)
//                .paymentMethod(PaymentMethod.CASH)
//                .specialRequest("Special request")
//                .build();
//
//        webTestClient.post()
//                .uri("/events")
//                .bodyValue(eventRequestModel)
//                .exchange()
//                .expectStatus().isCreated()
//                .expectBody(EventResponseModel.class);
//
//        StepVerifier.create(eventRepository.findAll())
//                .expectNextCount(3)
//                .verifyComplete();
//
//    }
//
//    @Test
//    void whenBookEvent_IncorrectEndpoint_thenReturn404() {
//        EventRequestModel eventRequestModel = EventRequestModel.builder()
//                .firstName("Jane")
//                .lastName("Doe")
//                .email("liondance@yopmail.com")
//                .phone("1234567890")
//                .address(
//                        new Address(
//                                "1234 Main St.",
//                                "Springfield",
//                                "Quebec",
//                                "J2X 2J4")
//                )
//                .eventDateTime(LocalDate.now().atTime(LocalTime.NOON))
//                .eventType(EventType.WEDDING)
//                .paymentMethod(PaymentMethod.CASH)
//                .specialRequest("Special request")
//                .build();
//
//        webTestClient.post()
//                .uri("/event")
//                .bodyValue(eventRequestModel)
//                .exchange()
//                .expectStatus().isNotFound();
//    }
//
//    @Test
//    void whenBookEvent_InvalidRequest_thenReturnBadRequestStatus() {
//        EventRequestModel requestModel = EventRequestModel.builder().build();
//
//        webTestClient.post()
//                .uri("/api/v1/events")
//                .bodyValue(requestModel)
//                .exchange()
//                .expectStatus().isBadRequest();
//    }
//
//    @Test
//    void whenBookEvent_NullRequest_thenReturnBadRequestStatus() {
//        webTestClient.post()
//                .uri("/api/v1/events")
//                .bodyValue(null)
//                .exchange()
//                .expectStatus().isBadRequest();
//    }
//
//    @Test
//    void whenBookEvent_EmptyRequest_thenReturnBadRequestStatus() {
//        webTestClient.post()
//                .uri("/api/v1/events")
//                .bodyValue(new EventRequestModel())
//                .exchange()
//                .expectStatus().isBadRequest();
//    }

}
