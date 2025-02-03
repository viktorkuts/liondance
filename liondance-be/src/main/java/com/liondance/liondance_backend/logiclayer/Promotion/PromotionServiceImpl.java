package com.liondance.liondance_backend.logiclayer.Promotion;

import com.liondance.liondance_backend.datalayer.Promotion.PromotionRepository;
import com.liondance.liondance_backend.presentationlayer.Promotion.PromotionRequestModel;
import com.liondance.liondance_backend.presentationlayer.Promotion.PromotionResponseModel;
import com.liondance.liondance_backend.utils.exceptions.InvalidInputException;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
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
        return promotionRepository.findByPromotionId(promotionId).map(PromotionResponseModel::from).switchIfEmpty(Mono.error( new NotFoundException("Promotion not found")));
    }

    @Override
    public Mono<PromotionResponseModel> updatePromotion(String promotionId, PromotionRequestModel promotionRequestModel) {
        // Validate discount rate (must be between 0 and 1 for percentage representation)
        if (promotionRequestModel.getDiscountRate() < 0 || promotionRequestModel.getDiscountRate() > 1) {
            return Mono.error(new InvalidInputException("Discount rate must be between 0% and 100%"));
        }

        return promotionRepository.findByPromotionId(promotionId)
                .switchIfEmpty(Mono.error(new NotFoundException("Promotion not found")))
                .flatMap(promotion -> {
                    promotion.setPromotionName(promotionRequestModel.getPromotionName());
                    promotion.setDiscountRate(promotionRequestModel.getDiscountRate());
                    promotion.setStartDate(promotionRequestModel.getStartDate());
                    promotion.setEndDate(promotionRequestModel.getEndDate());
                    promotion.setPromotionStatus(promotionRequestModel.getPromotionStatus());
                    return promotionRepository.save(promotion);
                })
                .map(PromotionResponseModel::from);
    }
}
