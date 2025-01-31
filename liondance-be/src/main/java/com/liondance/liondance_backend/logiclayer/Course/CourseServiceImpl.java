package com.liondance.liondance_backend.logiclayer.Course;

import com.liondance.liondance_backend.datalayer.Course.CourseRepository;
import com.liondance.liondance_backend.datalayer.Course.CourseStatus;
import com.liondance.liondance_backend.datalayer.User.UserRepository;
import com.liondance.liondance_backend.presentationlayer.Course.CourseResponseModel;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    public CourseServiceImpl(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Flux<CourseResponseModel> getAllCoursesByStudentId(String studentId) {
        System.out.println("Fetching courses for student ID: " + studentId);

        return courseRepository.getCoursesByUserIds(studentId)
                .doOnNext(course -> System.out.println("Found course: " + course))
                .switchIfEmpty(Mono.error(new NotFoundException("No courses found for student with id " + studentId)))
                .flatMap(course -> userRepository.findUserByUserId(course.getInstructorId())
                        .map(instructor -> {
                            System.out.println("Instructor found: " + instructor);
                            CourseResponseModel responseModel = CourseResponseModel.from(course);
                            responseModel.setInstructorFirstName(instructor.getFirstName());
                            responseModel.setInstructorMiddleName(instructor.getMiddleName());
                            responseModel.setInstructorLastName(instructor.getLastName());
                            return responseModel;
                        })
                );
    }

    @Override
    public Mono<CourseResponseModel> cancelCourse(String courseId, Mono<CourseStatus> status) {
        return courseRepository.findById(courseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Course with ID " + courseId + " not found")))
                .flatMap(course -> status.map(courseStatus -> {
                    course.setStatus(courseStatus);
                    return course;
                }))
                .flatMap(courseRepository::save)
                .map(CourseResponseModel::from);
    }
}
