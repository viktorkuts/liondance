package com.liondance.liondance_backend.datalayer.User;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<User> findUserByUserId (String userId);
    Flux<User> findUsersByRolesContaining(Role role);
}
