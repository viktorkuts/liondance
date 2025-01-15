package com.liondance.liondance_backend.datalayer.Promotion;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PromotionRepository extends ReactiveMongoRepository<Promotion, String> {
    Mono<Promotion> findByPromotionId(String promotionId);
}
