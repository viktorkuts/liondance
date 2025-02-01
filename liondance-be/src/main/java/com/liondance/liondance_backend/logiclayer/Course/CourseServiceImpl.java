package com.liondance.liondance_backend.logiclayer.Course;

import com.liondance.liondance_backend.datalayer.Course.CourseRepository;
import com.liondance.liondance_backend.datalayer.User.UserRepository;
import com.liondance.liondance_backend.presentationlayer.Course.CourseResponseModel;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import com.liondance.liondance_backend.datalayer.Notification.NotificationType;
import com.liondance.liondance_backend.logiclayer.Notification.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public CourseServiceImpl(CourseRepository courseRepository, UserRepository userRepository, NotificationService notificationService) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
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
                    return userRepository.findAll()
                            .collectList()
                            .flatMapMany(Flux::fromIterable)
                            .flatMap(student -> {
                                String formattedDates = cancelledDates.stream()
                                        .map(date -> date.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a")))
                                        .collect(Collectors.joining(", "));

                                String message = new StringBuilder()
                                        .append("Your class has been cancelled on the following date: ")
                                        .append(formattedDates)
                                        .append(".")
                                        .append("\n\nThank you for your understanding and see you next class!")
                                        .toString();

                                Boolean success = notificationService.sendMail(
                                        student.getEmail(),
                                        "Class Cancellation Notice",
                                        message,
                                        NotificationType.CLASS_CANCELLATION
                                );

                                if (!success) {
                                    throw new MailSendException("Failed to send email to " + student.getEmail());
                                }

                                return Mono.just(student);
                            })
                            .then(Mono.just(course));
                })
                .flatMap(courseRepository::save)
                .map(CourseResponseModel::from);
    }
}
