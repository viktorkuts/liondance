package com.liondance.liondance_backend.presentationlayer.Feedback;

import com.liondance.liondance_backend.datalayer.User.Client;
import com.liondance.liondance_backend.datalayer.User.Role;
import com.liondance.liondance_backend.datalayer.User.User;
import com.liondance.liondance_backend.datalayer.common.Address;
import com.liondance.liondance_backend.utils.WebTestAuthConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import static org.mockito.Mockito.when;
import com.liondance.liondance_backend.logiclayer.Feedback.FeedbackService;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;

import java.util.EnumSet;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.data.mongodb.port= 0"})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
class FeedbackControllerIntegrationTest {

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

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private FeedbackService feedbackService;

    @Test
    void getFeedbackByEventId_returnsFeedbacks_whenEventIdExists() {
        String eventId = "testEventId";
        FeedbackResponseModel feedback = FeedbackResponseModel.builder()
                .feedbackId("e4e02950-20ca-4010-87cb-8c20e3d3354b")
                .feedback("Great event!")
                .rating(5)
                .event(null)
                .build();

        when(feedbackService.getFeedbackByEventId(eventId)).thenReturn(Flux.just(feedback));

        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .get()
                .uri("/api/v1/feedbacks/event/" + eventId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FeedbackResponseModel.class)
                .hasSize(1)
                .contains(feedback);
    }

    @Test
    void getFeedbackByEventId_returnsNotFound_whenEventNotFound() {
        String eventId = "e5b07c6c6-dc48-489f-aa20-0d7d6fb12874gf";

        when(feedbackService.getFeedbackByEventId(eventId)).thenReturn(Flux.error(new NotFoundException("Event not found")));

        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .get()
                .uri("/api/v1/feedbacks/event/" + eventId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Event not found");
    }
}