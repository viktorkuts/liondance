package com.liondance.liondance_backend.logiclayer.Course;

import com.liondance.liondance_backend.datalayer.Course.CourseRepository;
import com.liondance.liondance_backend.datalayer.User.UserRepository;
import com.liondance.liondance_backend.presentationlayer.Course.CourseResponseModel;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    public CourseServiceImpl(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Flux<CourseResponseModel> getAllCourses() {
        System.out.println("Fetching all courses");

        return courseRepository.findAll()
                .doOnNext(course -> System.out.println("Found course: " + course))
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
    public Mono<CourseResponseModel> patchCancelledDates(String courseId, List<Instant> cancelledDates) {
        return courseRepository.findCourseByCourseId(courseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Course with ID " + courseId + " not found")))
                .flatMap(course -> {
                    course.setCancelledDates(cancelledDates);
                    return courseRepository.save(course);
                })
                .map(CourseResponseModel::from);
    }

}
