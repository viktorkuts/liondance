package com.liondance.liondance_backend.logiclayer.Course;

import com.liondance.liondance_backend.presentationlayer.Course.CourseResponseModel;
import reactor.core.publisher.Flux;

public interface CourseService {

    Flux<CourseResponseModel> getAllCoursesByStudentId(String studentId);
}
