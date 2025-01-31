package com.liondance.liondance_backend.logiclayer.Course;

import com.liondance.liondance_backend.datalayer.Course.CourseStatus;
import com.liondance.liondance_backend.presentationlayer.Course.CourseResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CourseService {

    Flux<CourseResponseModel> getAllCoursesByStudentId(String studentId);
    Mono<CourseResponseModel> cancelCourse(String courseId, Mono<CourseStatus> status);
}
