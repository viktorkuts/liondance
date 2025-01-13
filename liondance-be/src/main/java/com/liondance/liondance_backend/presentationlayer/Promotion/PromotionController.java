package com.liondance.liondance_backend.presentationlayer.Promotion;

import com.liondance.liondance_backend.logiclayer.Promotion.PromotionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/promotions")
public class PromotionController {
    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping
    public Flux<PromotionResponseModel> getAllPromotions() {
        return promotionService.getAllPromotions();
    }

    @GetMapping("/{promotionId}")
    public Mono<PromotionResponseModel> getPromotionById(@PathVariable String promotionId) {
        return promotionService.getPromotionById(promotionId);
    }
}
