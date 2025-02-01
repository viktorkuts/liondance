package com.liondance.liondance_backend.logiclayer.Course;

import com.liondance.liondance_backend.presentationlayer.Course.CourseResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface CourseService {

    Flux<CourseResponseModel> getAllCourses();
    Flux<CourseResponseModel> getAllCoursesByStudentId(String studentId);
    Mono<CourseResponseModel> patchCancelledDates(String courseId, List<Instant> cancelledDates);
}
