package com.liondance.liondance_backend.presentationlayer.Feedback;

import com.liondance.liondance_backend.TestSecurityConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import static org.mockito.Mockito.when;
import com.liondance.liondance_backend.logiclayer.Feedback.FeedbackService;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.data.mongodb.port= 0"}, classes = TestSecurityConfig.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
class FeedbackControllerIntegrationTest {

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

        webTestClient.get()
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

        webTestClient.get()
                .uri("/api/v1/feedbacks/event/" + eventId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Event not found");
    }
}