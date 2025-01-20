package com.liondance.liondance_backend.logiclayer.Course;

import com.liondance.liondance_backend.datalayer.Course.Course;
import com.liondance.liondance_backend.datalayer.Course.CourseRepository;
import com.liondance.liondance_backend.datalayer.User.*;
import com.liondance.liondance_backend.datalayer.common.Address;
import com.liondance.liondance_backend.presentationlayer.Course.CourseResponseModel;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplUnitTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course course1;
    private User instructor1;
    private Student student1;

    @BeforeEach
    void setUp() {
         course1 = Course.builder()
                .courseId("1")
                .name("Course 1")
                .instructorId("97e64875-97b1-4ada-b370-6609b6e518ac")
                .userIds(List.of("7876ea26-3f76-4e50-870f-5e5dad6d63d1"))
                .cancelledDates(new ArrayList<>())
                .dayOfWeek(DayOfWeek.SUNDAY)
                .startTime(LocalTime.parse("10:00", DateTimeFormatter.ofPattern("HH:mm")))
                .endTime(LocalTime.NOON)
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
                            courseResponseModel.getInstructorLastName().equals("Doe") &&
                            courseResponseModel.getUserIds().contains(studentId) &&
                            courseResponseModel.getDayOfWeek().equals(DayOfWeek.SUNDAY) &&
                            courseResponseModel.getStartTime().equals(LocalTime.parse("10:00", DateTimeFormatter.ofPattern("HH:mm"))) &&
                            courseResponseModel.getEndTime().equals(LocalTime.NOON)
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