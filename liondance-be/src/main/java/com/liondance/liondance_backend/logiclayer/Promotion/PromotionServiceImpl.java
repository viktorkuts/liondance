package com.liondance.liondance_backend.logiclayer.Promotion;

import com.liondance.liondance_backend.datalayer.Promotion.PromotionRepository;
import com.liondance.liondance_backend.presentationlayer.Promotion.PromotionResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Slf4j
@Service
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;

    public PromotionServiceImpl(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @Override
    public Flux<PromotionResponseModel> getAllPromotions() {
        return promotionRepository.findAll().map(PromotionResponseModel::from);
    }

    @Override
    public Mono<PromotionResponseModel> getPromotionById(String promotionId) {
        return promotionRepository.findByPromotionId(promotionId).map(PromotionResponseModel::from);
    }
}
