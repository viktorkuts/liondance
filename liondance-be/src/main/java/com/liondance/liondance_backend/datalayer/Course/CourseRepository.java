package com.liondance.liondance_backend.datalayer.Course;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CourseRepository extends ReactiveMongoRepository<Course, String> {
    @Query("{ 'userIds': ?0 }")
    Flux<Course> getCoursesByUserIds(String userId);

}
