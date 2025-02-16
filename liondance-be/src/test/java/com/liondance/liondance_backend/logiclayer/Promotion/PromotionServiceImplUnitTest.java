package com.liondance.liondance_backend.logiclayer.Promotion;

import com.liondance.liondance_backend.datalayer.Promotion.Promotion;
import com.liondance.liondance_backend.datalayer.Promotion.PromotionRepository;
import com.liondance.liondance_backend.datalayer.Promotion.PromotionStatus;
import com.liondance.liondance_backend.presentationlayer.Promotion.PromotionRequestModel;
import com.liondance.liondance_backend.presentationlayer.Promotion.PromotionResponseModel;
import com.liondance.liondance_backend.utils.exceptions.InvalidInputException;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromotionServiceImplUnitTest {

    @Mock
    private PromotionRepository promotionRepository;

    @InjectMocks
    private PromotionServiceImpl promotionService;

    private Promotion promotion1;
    private Promotion promotion2;

    @BeforeEach
    void setUp() {
        promotion1 = Promotion.builder()
                .id("1")
                .promotionId("PROMO1")
                .promotionName("Summer Sale")
                .discountRate(20.0)
                .startDate(LocalDate.parse("2025-06-01"))
                .endDate(LocalDate.parse("2025-06-30"))
                .promotionStatus(PromotionStatus.ACTIVE)
                .build();

        promotion2 = Promotion.builder()
                .id("2")
                .promotionId("PROMO2")
                .promotionName("Winter Sale")
                .discountRate(25.0)
                .startDate(LocalDate.parse("2025-12-01"))
                .endDate(LocalDate.parse("2025-12-31"))
                .promotionStatus(PromotionStatus.INACTIVE)
                .build();
    }

    @Test
    void whenGetAllPromotions_thenReturnAllPromotions() {
        Mockito.when(promotionRepository.findAll()).thenReturn(Flux.just(promotion1, promotion2));

        Flux<PromotionResponseModel> result = promotionService.getAllPromotions();

        StepVerifier.create(result)
                .expectNextMatches(promotionResponse ->
                        promotionResponse.getPromotionId().equals("PROMO1") &&
                                promotionResponse.getPromotionName().equals("Summer Sale") &&
                                promotionResponse.getDiscountRate().equals(20.0) &&
                                promotionResponse.getPromotionStatus() == PromotionStatus.ACTIVE
                )
                .expectNextMatches(promotionResponse ->
                        promotionResponse.getPromotionId().equals("PROMO2") &&
                                promotionResponse.getPromotionName().equals("Winter Sale") &&
                                promotionResponse.getDiscountRate().equals(25.0) &&
                                promotionResponse.getPromotionStatus() == PromotionStatus.INACTIVE
                )
                .verifyComplete();

        Mockito.verify(promotionRepository, Mockito.times(1)).findAll();
    }

    @Test
    void whenGetPromotionById_thenReturnPromotion() {
        String promotionId = "PROMO1";

        Mockito.when(promotionRepository.findByPromotionId(promotionId)).thenReturn(Mono.just(promotion1));

        Mono<PromotionResponseModel> result = promotionService.getPromotionById(promotionId);

        StepVerifier.create(result)
                .expectNextMatches(promotionResponse ->
                        promotionResponse.getPromotionId().equals("PROMO1") &&
                                promotionResponse.getPromotionName().equals("Summer Sale") &&
                                promotionResponse.getDiscountRate().equals(20.0) &&
                                promotionResponse.getPromotionStatus() == PromotionStatus.ACTIVE
                )
                .verifyComplete();

        Mockito.verify(promotionRepository, Mockito.times(1)).findByPromotionId(promotionId);
    }

    @Test
    void whenGetPromotionByInvalidId_thenThrowNotFoundException() {
        String invalidPromotionId = "INVALID_PROMO";

        Mockito.when(promotionRepository.findByPromotionId(invalidPromotionId)).thenReturn(Mono.empty());

        Mono<PromotionResponseModel> result = promotionService.getPromotionById(invalidPromotionId);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("Promotion not found"))
                .verify();

        Mockito.verify(promotionRepository, Mockito.times(1)).findByPromotionId(invalidPromotionId);
    }

    @Test
    void whenUpdatePromotionWithValidData_thenReturnUpdatedPromotion() {
        String promotionId = "PROMO1";
        PromotionRequestModel updateRequest = PromotionRequestModel.builder()
                .promotionName("Updated Summer Sale")
                .discountRate(0.20)
                .startDate(LocalDate.parse("2025-07-01"))
                .endDate(LocalDate.parse("2025-07-31"))
                .promotionStatus(PromotionStatus.INACTIVE)
                .build();

        Promotion updatedPromotion = Promotion.builder()
                .id("1")
                .promotionId("PROMO1")
                .promotionName(updateRequest.getPromotionName())
                .discountRate(updateRequest.getDiscountRate())
                .startDate(updateRequest.getStartDate())
                .endDate(updateRequest.getEndDate())
                .promotionStatus(updateRequest.getPromotionStatus())
                .build();

        when(promotionRepository.findByPromotionId(promotionId)).thenReturn(Mono.just(promotion1));
        when(promotionRepository.save(Mockito.any(Promotion.class))).thenReturn(Mono.just(updatedPromotion));

        Mono<PromotionResponseModel> result = promotionService.updatePromotion(promotionId, updateRequest);

        StepVerifier.create(result)
                .expectNextMatches(promotionResponse ->
                        promotionResponse.getPromotionId().equals("PROMO1") &&
                                promotionResponse.getPromotionName().equals("Updated Summer Sale") &&
                                promotionResponse.getDiscountRate().equals(0.20) &&
                                promotionResponse.getStartDate().equals(LocalDate.parse("2025-07-01")) &&
                                promotionResponse.getEndDate().equals(LocalDate.parse("2025-07-31")) &&
                                promotionResponse.getPromotionStatus() == PromotionStatus.INACTIVE
                )
                .verifyComplete();

        Mockito.verify(promotionRepository).findByPromotionId(promotionId);
        Mockito.verify(promotionRepository).save(Mockito.any(Promotion.class));
    }

    @Test
    void whenUpdatePromotionWithInvalidDiscountRate_thenThrowInvalidInputException() {
        String promotionId = "PROMO1";
        PromotionRequestModel updateRequest = PromotionRequestModel.builder()
                .promotionName("Updated Summer Sale")
                .discountRate(1.5) // Invalid discount rate > 1
                .startDate(LocalDate.parse("2025-07-01"))
                .endDate(LocalDate.parse("2025-07-31"))
                .promotionStatus(PromotionStatus.ACTIVE)
                .build();

        Mono<PromotionResponseModel> result = promotionService.updatePromotion(promotionId, updateRequest);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof InvalidInputException &&
                                throwable.getMessage().equals("Discount rate must be between 0% and 100%"))
                .verify();

        Mockito.verify(promotionRepository, Mockito.never()).findByPromotionId(Mockito.anyString());
        Mockito.verify(promotionRepository, Mockito.never()).save(Mockito.any(Promotion.class));
    }

    @Test
    void whenUpdatePromotionWithInvalidId_thenThrowNotFoundException() {
        String invalidPromotionId = "INVALID_PROMO";
        PromotionRequestModel updateRequest = PromotionRequestModel.builder()
                .promotionName("Updated Summer Sale")
                .discountRate(0.20)
                .startDate(LocalDate.parse("2025-07-01"))
                .endDate(LocalDate.parse("2025-07-31"))
                .promotionStatus(PromotionStatus.ACTIVE)
                .build();

        when(promotionRepository.findByPromotionId(invalidPromotionId)).thenReturn(Mono.empty());

        Mono<PromotionResponseModel> result = promotionService.updatePromotion(invalidPromotionId, updateRequest);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Promotion not found"))
                .verify();

        Mockito.verify(promotionRepository).findByPromotionId(invalidPromotionId);
        Mockito.verify(promotionRepository, Mockito.never()).save(Mockito.any(Promotion.class));
    }

    @Test
    void whenCreatePromotion_withValidRequest_thenReturnCreatedPromotion() {
        PromotionRequestModel requestModel = PromotionRequestModel.builder()
                .promotionName("New Year Sale")
                .discountRate(0.30)
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 1, 31))
                .promotionStatus(PromotionStatus.ACTIVE)
                .build();


        Promotion toSave = PromotionRequestModel.from(requestModel);
        toSave.setId("123");

        Mockito.when(promotionRepository.save(Mockito.any(Promotion.class)))
                .thenReturn(Mono.just(toSave));

        Mono<PromotionResponseModel> result = promotionService.createPromotion(requestModel);

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getPromotionName().equals("New Year Sale") &&
                                response.getDiscountRate().equals(0.30) &&
                                response.getPromotionStatus() == PromotionStatus.ACTIVE
                )
                .verifyComplete();

        Mockito.verify(promotionRepository, Mockito.times(1)).save(Mockito.any(Promotion.class));
    }

    @Test
    void whenCreatePromotion_withInvalidDiscount_thenThrowInvalidInputException() {
        PromotionRequestModel requestModel = PromotionRequestModel.builder()
                .promotionName("Bad Discount")
                .discountRate(1.5) // invalid discount
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 1, 31))
                .promotionStatus(PromotionStatus.ACTIVE)
                .build();

        Mono<PromotionResponseModel> result = promotionService.createPromotion(requestModel);

        StepVerifier.create(result)
                .expectErrorMatches(ex -> ex instanceof InvalidInputException &&
                        ex.getMessage().contains("Discount rate must be between 0% and 100%"))
                .verify();

        Mockito.verify(promotionRepository, Mockito.never()).save(Mockito.any(Promotion.class));
    }

    @Test
    void whenDeletePromotion_withExistingPromotionId_thenDeleteSuccessfully() {
        Mockito.when(promotionRepository.findByPromotionId("PROMO1"))
                .thenReturn(Mono.just(promotion1));

        Mockito.when(promotionRepository.delete(promotion1))
                .thenReturn(Mono.empty());

        Mono<Void> result = promotionService.deletePromotion("PROMO1");

        StepVerifier.create(result)
                .verifyComplete();

        Mockito.verify(promotionRepository).findByPromotionId("PROMO1");
        Mockito.verify(promotionRepository).delete(promotion1);
    }

    @Test
    void whenDeletePromotion_withNonExistingPromotionId_thenThrowNotFoundException() {
        Mockito.when(promotionRepository.findByPromotionId("DOES_NOT_EXIST"))
                .thenReturn(Mono.empty());

        Mono<Void> result = promotionService.deletePromotion("DOES_NOT_EXIST");

        StepVerifier.create(result)
                .expectErrorMatches(ex -> ex instanceof NotFoundException &&
                        ex.getMessage().contains("Promotion not found"))
                .verify();

        Mockito.verify(promotionRepository).findByPromotionId("DOES_NOT_EXIST");
        Mockito.verify(promotionRepository, Mockito.never()).delete(Mockito.any());
    }

}
