package com.liondance.liondance_backend.logiclayer.User;

import com.liondance.liondance_backend.datalayer.Course.Course;
import com.liondance.liondance_backend.datalayer.Course.CourseRepository;
import com.liondance.liondance_backend.datalayer.User.*;
import com.liondance.liondance_backend.presentationlayer.Course.CourseResponseModel;
import com.liondance.liondance_backend.presentationlayer.User.UserResponseModel;
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

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        student1 = Student.builder()
                .userId("7876ea26-3f76-4e50-870f-5e5dad6d63d1")
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@null.local")
                .dob(LocalDate.parse("1995-01-01"))
                .gender(Gender.FEMALE)
                .roles(EnumSet.of(Role.STUDENT))
                .joinDate(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
                .registrationStatus(RegistrationStatus.ACTIVE)
                .build();

        student2 = Student.builder()
                .userId("7876ea26-3f76-4e50-870f-5e5dad6d63d1")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@null.local")
                .dob(LocalDate.parse("2000-01-01"))
                .gender(Gender.MALE)
                .roles(EnumSet.of(Role.STUDENT))
                .joinDate(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
                .registrationStatus(RegistrationStatus.PENDING)
                .build();
    }

    @Test
    void whenGetStudentsByRegistrationStatuses_thenReturnMatchingStudents() {
        List<RegistrationStatus> statuses = List.of(RegistrationStatus.PENDING);
        Mockito.when(userRepository.findStudentsByRegistrationStatuses(statuses))
                .thenReturn(Flux.just(student2));
        Flux<UserResponseModel> result = userService.getStudentsByRegistrationStatuses(statuses);
        StepVerifier.create(result)
                .expectNext(UserResponseModel.from(student2))
                .verifyComplete();
        Mockito.verify(userRepository, Mockito.times(1))
                .findStudentsByRegistrationStatuses(statuses);
    }

    @Test
    void whenGetUserByUserId_thenReturnUser() {
        String userId = "7876ea26-3f76-4e50-870f-5e5dad6d63d1";
        Mockito.when(userRepository.findUserByUserId(userId))
                .thenReturn(Mono.just(student1));
        Mono<UserResponseModel> result = userService.getUserByUserId(userId);
        StepVerifier.create(result)
                .expectNext(UserResponseModel.from(student1))
                .verifyComplete();
        Mockito.verify(userRepository, Mockito.times(1))
                .findUserByUserId(userId);
    }
    @Test
    void whenGetUserByUserId_thenThrowNotFoundException() {
        String userId = "7876ea26-3f76-4e50-870f-5e5dad6d63d1a";
        Mockito.when(userRepository.findUserByUserId(userId))
                .thenReturn(Mono.empty());
        Mono<UserResponseModel> result = userService.getUserByUserId(userId);
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("User with userId: " + userId + " not found"))
                .verify();
        Mockito.verify(userRepository, Mockito.times(1))
                .findUserByUserId(userId);
    }
}