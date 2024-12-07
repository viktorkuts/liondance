package com.liondance.liondance_backend.datalayer.Course;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CourseRepository extends ReactiveMongoRepository<Course, String> {

    Flux<Course> getCoursesByUserIdsContaining(String userId);

}
