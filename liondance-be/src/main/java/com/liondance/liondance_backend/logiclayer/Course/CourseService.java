package com.liondance.liondance_backend.logiclayer.Course;

import com.liondance.liondance_backend.presentationlayer.Course.CourseResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface CourseService {

    Flux<CourseResponseModel> getAllCoursesByCourseId(String courseId);
    Flux<CourseResponseModel> getAllCoursesByStudentId(String studentId);
    Mono<CourseResponseModel> cancelCourse(String courseId, LocalDate cancelledDates);
}
