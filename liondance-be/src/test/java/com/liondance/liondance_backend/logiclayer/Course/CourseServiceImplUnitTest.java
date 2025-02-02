package com.liondance.liondance_backend.logiclayer.Course;

import com.liondance.liondance_backend.datalayer.Course.Course;
import com.liondance.liondance_backend.datalayer.Course.CourseRepository;
import com.liondance.liondance_backend.datalayer.User.*;
import com.liondance.liondance_backend.datalayer.common.Address;
import com.liondance.liondance_backend.logiclayer.Notification.NotificationService;
import com.liondance.liondance_backend.presentationlayer.Course.CourseResponseModel;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSendException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CourseServiceImplUnitTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course course1;
    private User instructor1;
    private Student student1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        course1 = Course.builder()
                .courseId("1")
                .name("Course 1")
                .instructorId("97e64875-97b1-4ada-b370-6609b6e518ac")
                .userIds(List.of("7876ea26-3f76-4e50-870f-5e5dad6d63d1"))
                .cancelledDates(new ArrayList<>())
                .dayOfWeek(DayOfWeek.SUNDAY)
                .startTime(Instant.parse("2025-12-25T10:00:00Z"))
                .endTime(Instant.parse("2025-12-25T12:00:00Z"))
                .build();

        instructor1 = User.builder()
                .userId("97e64875-97b1-4ada-b370-6609b6e518ac")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@null.local")
                .dob(LocalDate.parse("1990-01-01"))
                .roles(EnumSet.of(Role.STAFF))
                .address(Address.builder()
                        .streetAddress("123 Main St")
                        .city("Montreal")
                        .state("QC")
                        .zip("H0H 0H0")
                        .build()
                )
                .build();

        student1 = Student.builder()
                .userId("7876ea26-3f76-4e50-870f-5e5dad6d63d1")
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@null.local")
                .dob(LocalDate.parse("1995-01-01"))
                .roles(EnumSet.of(Role.STUDENT))
                .joinDate(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
                .registrationStatus(RegistrationStatus.ACTIVE)
                .build();
    }

    @Test
    void getAllCourses() {
        Course course = new Course();
        course.setInstructorId("instructorId");
        User instructor = new User();
        instructor.setFirstName("John");
        instructor.setMiddleName("Doe");
        instructor.setLastName("Smith");

        when(courseRepository.findAll()).thenReturn(Flux.just(course));
        when(userRepository.findUserByUserId("instructorId")).thenReturn(Mono.just(instructor));

        StepVerifier.create(courseService.getAllCourses())
                .expectNextMatches(courseResponseModel ->
                        courseResponseModel.getInstructorFirstName().equals("John") &&
                                courseResponseModel.getInstructorMiddleName().equals("Doe") &&
                                courseResponseModel.getInstructorLastName().equals("Smith"))
                .verifyComplete();

        verify(courseRepository, times(1)).findAll();
        verify(userRepository, times(1)).findUserByUserId("instructorId");
    }

    @Test
    void getAllCoursesByStudentId() {
        String studentId = "studentId";
        Course course = new Course();
        course.setInstructorId("instructorId");
        User instructor = new User();
        instructor.setFirstName("John");
        instructor.setMiddleName("Doe");
        instructor.setLastName("Smith");

        when(courseRepository.getCoursesByUserIds(studentId)).thenReturn(Flux.just(course));
        when(userRepository.findUserByUserId("instructorId")).thenReturn(Mono.just(instructor));

        StepVerifier.create(courseService.getAllCoursesByStudentId(studentId))
                .expectNextMatches(courseResponseModel ->
                        courseResponseModel.getInstructorFirstName().equals("John") &&
                                courseResponseModel.getInstructorMiddleName().equals("Doe") &&
                                courseResponseModel.getInstructorLastName().equals("Smith"))
                .verifyComplete();

        verify(courseRepository, times(1)).getCoursesByUserIds(studentId);
        verify(userRepository, times(1)).findUserByUserId("instructorId");
    }

    @Test
    void getAllCourses_noCoursesFound() {
        when(courseRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(courseService.getAllCourses())
                .expectNextCount(0)
                .verifyComplete();

        verify(courseRepository, times(1)).findAll();
        verify(userRepository, never()).findUserByUserId(anyString());
    }

    @Test
    void getAllCoursesByStudentId_noCoursesFound() {
        String studentId = "studentId";

        when(courseRepository.getCoursesByUserIds(studentId)).thenReturn(Flux.empty());

        StepVerifier.create(courseService.getAllCoursesByStudentId(studentId))
                .expectError(NotFoundException.class)
                .verify();

        verify(courseRepository, times(1)).getCoursesByUserIds(studentId);
        verify(userRepository, never()).findUserByUserId(anyString());
    }

    @Test
    void patchCancelledDates() {
        String courseId = "courseId";
        List<Instant> cancelledDates = List.of(Instant.now());
        Course course = new Course();
        course.setCancelledDates(cancelledDates);
        User student = new User();
        student.setEmail("student@example.com");

        when(courseRepository.findCourseByCourseId(courseId)).thenReturn(Mono.just(course));
        when(userRepository.findAll()).thenReturn(Flux.just(student));
        when(notificationService.sendMail(anyString(), anyString(), anyString(), any())).thenReturn(true);
        when(courseRepository.save(any(Course.class))).thenReturn(Mono.just(course));

        StepVerifier.create(courseService.patchCancelledDates(courseId, cancelledDates))
                .expectNextMatches(courseResponseModel ->
                        courseResponseModel.getCancelledDates().equals(cancelledDates))
                .verifyComplete();

        verify(courseRepository, times(1)).findCourseByCourseId(courseId);
        verify(userRepository, times(1)).findAll();
        verify(notificationService, times(1)).sendMail(anyString(), anyString(), anyString(), any());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void patchCancelledDates_courseNotFound() {
        String courseId = "courseId";
        List<Instant> cancelledDates = List.of(Instant.now());

        when(courseRepository.findCourseByCourseId(courseId)).thenReturn(Mono.empty());

        StepVerifier.create(courseService.patchCancelledDates(courseId, cancelledDates))
                .expectError(NotFoundException.class)
                .verify();

        verify(courseRepository, times(1)).findCourseByCourseId(courseId);
        verify(userRepository, never()).findAll();
        verify(notificationService, never()).sendMail(anyString(), anyString(), anyString(), any());
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void patchCancelledDates_mailSendException() {
        String courseId = "courseId";
        List<Instant> cancelledDates = List.of(Instant.now());
        Course course = new Course();
        course.setCancelledDates(cancelledDates);
        User student = new User();
        student.setEmail("student@example.com");

        when(courseRepository.findCourseByCourseId(courseId)).thenReturn(Mono.just(course));
        when(userRepository.findAll()).thenReturn(Flux.just(student));
        when(notificationService.sendMail(anyString(), anyString(), anyString(), any())).thenReturn(false);

        StepVerifier.create(courseService.patchCancelledDates(courseId, cancelledDates))
                .expectError(MailSendException.class)
                .verify();

        verify(courseRepository, times(1)).findCourseByCourseId(courseId);
        verify(userRepository, times(1)).findAll();
        verify(notificationService, times(1)).sendMail(anyString(), anyString(), anyString(), any());
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void whenGetAllCoursesByStudentId_thenReturnCourses() {
        String studentId = "7876ea26-3f76-4e50-870f-5e5dad6d63d1";

        Mockito.when(courseRepository.getCoursesByUserIds(studentId)).thenReturn(Flux.just(course1));
        Mockito.when(userRepository.findUserByUserId(course1.getInstructorId())).thenReturn(Mono.just(instructor1));

        Flux<CourseResponseModel> result = courseService.getAllCoursesByStudentId(studentId);

        StepVerifier.create(result)
                .expectNextMatches(courseResponseModel ->
                        courseResponseModel.getCourseId().equals("1") &&
                                courseResponseModel.getName().equals("Course 1") &&
                                courseResponseModel.getInstructorFirstName().equals("John") &&
                                courseResponseModel.getInstructorMiddleName() == null &&
                                courseResponseModel.getInstructorLastName().equals("Doe") &&
                                courseResponseModel.getUserIds().contains(studentId) &&
                                courseResponseModel.getDayOfWeek().equals(DayOfWeek.SUNDAY) &&
                                courseResponseModel.getStartTime().equals(Instant.parse("2025-12-25T10:00:00Z")) &&
                                courseResponseModel.getEndTime().equals(Instant.parse("2025-12-25T12:00:00Z"))
                )
                .verifyComplete();

        Mockito.verify(courseRepository, times(1)).getCoursesByUserIds(studentId);
        Mockito.verify(userRepository, times(1)).findUserByUserId(course1.getInstructorId());
    }

    @Test
    void whenGetAllCoursesByInvalidStudentId_thenThrowNotFoundException() {
        String invalidStudentId = "invalid-student-id";

        Mockito.when(courseRepository.getCoursesByUserIds(invalidStudentId)).thenReturn(Flux.empty());

        Flux<CourseResponseModel> result = courseService.getAllCoursesByStudentId(invalidStudentId);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("No courses found for student with id " + invalidStudentId))
                .verify();

        Mockito.verify(courseRepository, times(1)).getCoursesByUserIds(invalidStudentId);
        Mockito.verifyNoInteractions(userRepository);
    }
}