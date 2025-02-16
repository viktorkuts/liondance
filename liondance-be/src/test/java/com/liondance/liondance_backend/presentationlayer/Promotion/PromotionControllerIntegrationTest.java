package com.liondance.liondance_backend.presentationlayer.Promotion;

import com.liondance.liondance_backend.datalayer.Promotion.Promotion;
import com.liondance.liondance_backend.datalayer.Promotion.PromotionRepository;
import com.liondance.liondance_backend.datalayer.Promotion.PromotionStatus;
import com.liondance.liondance_backend.datalayer.User.Client;
import com.liondance.liondance_backend.datalayer.User.Role;
import com.liondance.liondance_backend.datalayer.User.User;
import com.liondance.liondance_backend.datalayer.common.Address;
import com.liondance.liondance_backend.utils.WebTestAuthConfig;
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
import java.util.EnumSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.data.mongodb.port= 0"})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
class PromotionControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PromotionRepository promotionRepository;

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
            .roles(EnumSet.of(Role.STAFF, Role.ADMIN))
            .associatedId("thetesterstaff")
            .build();

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

    @Test
    void WhenUpdateValidPromotion_ThenReturnUpdatedPromotion() {
        PromotionRequestModel updateRequest = PromotionRequestModel.builder()
                .promotionName("Updated Black Friday Sale")
                .startDate(LocalDate.of(2025, 11, 1))
                .endDate(LocalDate.of(2025, 11, 30))
                .discountRate(0.30)
                .promotionStatus(PromotionStatus.ACTIVE)
                .build();

        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .patch()
                .uri("/api/v1/promotions/{promotionId}", promotion1.getPromotionId())
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PromotionResponseModel.class)
                .isEqualTo(PromotionResponseModel.builder()
                        .promotionId(promotion1.getPromotionId())
                        .promotionName(updateRequest.getPromotionName())
                        .startDate(updateRequest.getStartDate())
                        .endDate(updateRequest.getEndDate())
                        .discountRate(updateRequest.getDiscountRate())
                        .promotionStatus(updateRequest.getPromotionStatus())
                        .build());
    }

    @Test
    void WhenUpdatePromotionWithInvalidId_ThenReturnNotFound() {
        String invalidId = "invalid-promotion-id";
        PromotionRequestModel updateRequest = PromotionRequestModel.builder()
                .promotionName("Updated Promotion")
                .startDate(LocalDate.of(2025, 11, 1))
                .endDate(LocalDate.of(2025, 11, 30))
                .discountRate(0.30)
                .promotionStatus(PromotionStatus.ACTIVE)
                .build();

        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .patch()
                .uri("/api/v1/promotions/{promotionId}", invalidId)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    void WhenUpdatePromotionWithInvalidDiscountRate_ThenReturnBadRequest() {
        PromotionRequestModel updateRequest = PromotionRequestModel.builder()
                .promotionName("Updated Black Friday Sale")
                .startDate(LocalDate.of(2025, 11, 1))
                .endDate(LocalDate.of(2025, 11, 30))
                .discountRate(120.0)
                .promotionStatus(PromotionStatus.ACTIVE)
                .build();

        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .patch()
                .uri("/api/v1/promotions/{promotionId}", promotion1.getPromotionId())
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isEqualTo(422);
    }

    @Test
    void whenCreatePromotion_withValidData_thenReturnCreatedPromotion() {
        PromotionRequestModel requestModel = PromotionRequestModel.builder()
                .promotionName("New Year Sale")
                .discountRate(0.30)
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 1, 15))
                .promotionStatus(PromotionStatus.ACTIVE)
                .build();

        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .post()
                .uri("/api/v1/promotions")
                .bodyValue(requestModel)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PromotionResponseModel.class)
                .value(response -> {

                    assertNotNull(response.getPromotionId());
                    assertEquals("New Year Sale", response.getPromotionName());
                    assertEquals(0.30, response.getDiscountRate());
                    assertEquals(PromotionStatus.ACTIVE, response.getPromotionStatus());
                });


        StepVerifier.create(promotionRepository.findAll())
                .expectNextCount(4) // we started with 3
                .verifyComplete();
    }

    @Test
    void whenCreatePromotion_withInvalidDiscount_thenReturnError() {
        PromotionRequestModel requestModel = PromotionRequestModel.builder()
                .promotionName("Impossible Discount")
                .discountRate(1.5)
                .startDate(LocalDate.of(2025, 2, 1))
                .endDate(LocalDate.of(2025, 2, 10))
                .promotionStatus(PromotionStatus.ACTIVE)
                .build();

        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .post()
                .uri("/api/v1/promotions")
                .bodyValue(requestModel)
                .exchange()
                .expectStatus().is4xxClientError();

        StepVerifier.create(promotionRepository.findAll())
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void whenDeletePromotion_withValidId_thenPromotionIsDeleted() {

        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .delete()
                .uri("/api/v1/promotions/{promotionId}", promotion1.getPromotionId())
                .exchange()
                .expectStatus().isNoContent();


        StepVerifier.create(promotionRepository.findByPromotionId(promotion1.getPromotionId()))
                .verifyComplete();
    }

    @Test
    void whenDeletePromotion_withNonExistentId_thenNotFound() {
        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .delete()
                .uri("/api/v1/promotions/{promotionId}", "DOES_NOT_EXIST")
                .exchange()
                .expectStatus().isNotFound();
    }

}