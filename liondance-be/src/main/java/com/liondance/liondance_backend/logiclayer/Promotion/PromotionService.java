package com.liondance.liondance_backend.logiclayer.Promotion;

import com.liondance.liondance_backend.presentationlayer.Promotion.PromotionRequestModel;
import com.liondance.liondance_backend.presentationlayer.Promotion.PromotionResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PromotionService {
    Flux<PromotionResponseModel> getAllPromotions();
    Mono<PromotionResponseModel> getPromotionById(String promotionId);
    Mono<PromotionResponseModel> updatePromotion(String promotionId, PromotionRequestModel promotionRequestModel);
}
