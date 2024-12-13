package com.liondance.liondance_backend.datalayer.User;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import java.util.List;

import java.util.List;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findUserByEmail(String email);
    Mono<User> findUserByUserId (String userId);
    Flux<User> findUsersByRolesContaining(Role role);
    @Query("{ 'registrationStatus': { $in: ?0 }, 'roles': { $elemMatch: { $eq: 'STUDENT' } } }")
    Flux<Student> findStudentsByRegistrationStatuses(List<RegistrationStatus> statuses);
    Mono<User> findByUserId(String userId);


}
