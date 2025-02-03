package com.liondance.liondance_backend.presentationlayer.ClassFeedback;


import com.liondance.liondance_backend.datalayer.ClassFeedback.ClassFeedbackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.data.mongodb.port= 0"})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
class ClassFeedbackControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private ClassFeedbackRepository classFeedbackRepository;


    @Test
    void addClassFeedback() {
        ClassFeedbackRequestModel requestModel = ClassFeedbackRequestModel.builder()
                .classDate(LocalDate.now())
                .score(4.5)
                .comment("Great class!")
                .build();

        webTestClient.post()
                .uri("/api/v1/classfeedback")
                .body(Mono.just(requestModel), ClassFeedbackRequestModel.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClassFeedbackResponseModel.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals(requestModel.getScore(), response.getScore());
                    assertEquals(requestModel.getComment(), response.getComment());
                });
    }

}