package com.liondance.liondance_backend.datalayer.User;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Flux<User> findUsersByRolesContaining(Role role);
}
