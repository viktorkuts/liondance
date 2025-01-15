package com.liondance.liondance_backend.presentationlayer.Promotion;

import com.liondance.liondance_backend.datalayer.Promotion.Promotion;
import com.liondance.liondance_backend.datalayer.Promotion.PromotionRepository;
import com.liondance.liondance_backend.datalayer.Promotion.PromotionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.concurrent.Flow;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.data.mongodb.port= 0"})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
class PromotionControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PromotionRepository promotionRepository;

Promotion promotion1 =  Promotion.builder()
        .promotionId("df428a05-9174-4189-aff8-d0d60bd5a530")
        .promotionName("Black Friday Sale")
        .startDate(LocalDate.of(2025, 11, 1))
        .endDate(LocalDate.of(2025, 11, 30))
        .discountRate(0.25)
        .promotionStatus(PromotionStatus.INACTIVE)
        .build();

Promotion promotion2 = Promotion.builder()
        .promotionId("c7ce931c-0455-43e5-8868-706a406cfb57")
        .promotionName("Back to School Sale")
        .startDate(LocalDate.of(2025, 9, 1))
        .endDate(LocalDate.of(2025, 9, 30))
        .discountRate(0.15)
        .promotionStatus(PromotionStatus.INACTIVE)
        .build();

Promotion promotion3 =   Promotion.builder()
        .promotionId("e876774d-ca7d-40cd-b828-cc007bff3b82")
        .promotionName("Summer Sale")
        .startDate(LocalDate.of(2025, 6, 1))
        .endDate(LocalDate.of(2025, 8, 31))
        .discountRate(0.20)
        .promotionStatus(PromotionStatus.INACTIVE)
        .build();

@BeforeEach
 void setUp() {
    StepVerifier.create(promotionRepository.deleteAll())
            .verifyComplete();

    Publisher<Promotion> promotionPublisher = Flux.just(promotion1, promotion2, promotion3)
            .flatMap(promotionRepository::save);

    StepVerifier.create(promotionPublisher).expectNextCount(3).verifyComplete();
}



    @Test
    void WhenGetAllPromotions_ReturnAllPromotions() {
        webTestClient.get().uri("/api/v1/promotions")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PromotionResponseModel.class)
                .hasSize(3);
    }

   @Test
    void WhenGetPromotionById_ReturnPromotion() {
        webTestClient.get().uri("/api/v1/promotions/{promotionId}", promotion1.getPromotionId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(PromotionResponseModel.class)
                .isEqualTo(PromotionResponseModel.from(promotion1));
    }

    @Test
    void WhenGetPromotionByInvalidId_ReturnNotFound() {
        webTestClient.get().uri("/api/v1/promotions/{promotionId}", "invalidId")
                .exchange()
                .expectStatus().isNotFound();
    }

}