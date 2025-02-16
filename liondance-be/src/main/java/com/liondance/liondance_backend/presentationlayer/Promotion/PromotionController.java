package com.liondance.liondance_backend.presentationlayer.Promotion;

import com.liondance.liondance_backend.logiclayer.Promotion.PromotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/promotions")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
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

    @PreAuthorize("hasAuthority('STAFF')")
    @PatchMapping("/{promotionId}")
    public Mono<PromotionResponseModel> updatePromotion(@PathVariable String promotionId, @RequestBody PromotionRequestModel promotionRequestModel) {
        return promotionService.updatePromotion(promotionId, promotionRequestModel);
    }

    @PreAuthorize("hasAuthority('STAFF')")
    @PostMapping
    public Mono<PromotionResponseModel> createPromotion(
            @RequestBody PromotionRequestModel promotionRequestModel) {
        return promotionService.createPromotion(promotionRequestModel);
    }

    @PreAuthorize("hasAuthority('STAFF')")
    @DeleteMapping("/{promotionId}")
    public Mono<ResponseEntity<Void>> deletePromotion(@PathVariable String promotionId) {
        return promotionService.deletePromotion(promotionId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
