package com.liondance.liondance_backend.datalayer.User;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Flux<User> findUsersByRolesContaining(Role role);
    @Query("{ 'registrationStatus': { $in: ?0 }, 'roles': { $elemMatch: { $eq: 'STUDENT' } } }")
    Flux<Student> findStudentsByRegistrationStatuses(List<RegistrationStatus> statuses);
}
