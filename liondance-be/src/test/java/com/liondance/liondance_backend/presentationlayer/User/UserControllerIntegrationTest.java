package com.liondance.liondance_backend.presentationlayer.User;

import com.liondance.liondance_backend.datalayer.Course.Course;
import com.liondance.liondance_backend.datalayer.Course.CourseRepository;
import com.liondance.liondance_backend.datalayer.User.*;
import com.liondance.liondance_backend.datalayer.common.Address;
import com.liondance.liondance_backend.presentationlayer.Course.CourseResponseModel;
import com.liondance.liondance_backend.utils.WebTestAuthConfig;
import com.liondance.liondance_backend.utils.exceptions.EmailInUse;
import com.liondance.liondance_backend.utils.exceptions.InvalidInputException;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import com.liondance.liondance_backend.utils.exceptions.StudentNotPending;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.reactivestreams.Publisher;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.data.mongodb.port= 0"})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
class UserControllerIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private WebTestClient client;

    User testUser = User.builder()
            .userId("test-user-id")
            .firstName("Test")
            .lastName("User")
            .email("test.user@null.local")
            .dob(LocalDate.parse("1990-01-01"))
            .roles(EnumSet.of(Role.CLIENT))
            .isSubscribed(false)
            .address(Address.builder()
                    .streetAddress("123 Test St")
                    .city("TestCity")
                    .state("TC")
                    .zip("T0T 0T0")
                    .build())
            .build();

    User staff = Client.builder()
            .userId(UUID.randomUUID().toString())
            .firstName("JaneStaff")
            .lastName("DoeStaff")
            .email("liondance@yopmail.com")
            .phone("1234567890")
            .address(
                    new Address(
                            "1234 Main St.",
                            "Springfield",
                            "Quebec",
                            "J2X 2J4")
            )
            .roles(EnumSet.of(Role.STAFF, Role.ADMIN))
            .associatedId("thetesterstaff")
            .build();

    Course course1 = Course.builder()
            .courseId("1")
            .name("Course 1")
            .instructorId("97e64875-97b1-4ada-b370-6609b6e518ac")
            .userIds(List.of("7876ea26-3f76-4e50-870f-5e5dad6d63d1"))
            .cancelledDates(new ArrayList<>())
            .dayOfWeek(DayOfWeek.SUNDAY)
            .startTime(LocalTime.parse("10:00", DateTimeFormatter.ofPattern("HH:mm")).atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant())
            .endTime(LocalTime.NOON.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant())
            .build();

    User user1 = User.builder()
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

    Student student1 = Student.builder()
            .userId("7876ea26-3f76-4e50-870f-5e5dad6d63d1")
            .firstName("Jane")
            .lastName("Doe")
            .email("jane.doe@null.local")
            .dob(LocalDate.parse("1995-01-01"))
            .roles(EnumSet.of(Role.STUDENT))
            .address(Address.builder()
                    .streetAddress("456 Main St")
                    .city("Montreal")
                    .state("QC")
                    .zip("H0H 0H0")
                    .build()
            )
            .joinDate(Instant.now())
            .registrationStatus(RegistrationStatus.ACTIVE)
            .build();
    Student student2 = Student.builder()
            .userId("7876ea26-3f76-4e50-870f-5e5dad6d63d2")
            .firstName("John")
            .lastName("Doe")
            .email("john.doe-same-same-but-different@null.local")
            .dob(LocalDate.parse("2000-01-01"))
            .roles(EnumSet.of(Role.STUDENT))
            .address(Address.builder()
                    .streetAddress("500 Main St")
                    .city("Montreal")
                    .state("QC")
                    .zip("H1H 0H0")
                    .build()
            )
            .joinDate(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
            .registrationStatus(RegistrationStatus.PENDING)
            .build();

    @BeforeEach
    public void setupDB() {
        StepVerifier.create(userRepository.deleteAll())
                .verifyComplete();
        StepVerifier.create(courseRepository.deleteAll())
                .verifyComplete();

        Publisher<User> userPublisher = Flux.just(user1, student1, student2)
                .flatMap(userRepository::save);

        Publisher<Course> coursePublisher = Flux.just(course1).flatMap(courseRepository::save);

        StepVerifier.create(userPublisher)
                .expectNextCount(3)
                .verifyComplete();

        StepVerifier.create(coursePublisher)
                .expectNextCount(1)
                .verifyComplete();

    }

    @Test
    void whenGetAllUsers_thenReturnUserResponseModels() {
        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .get()
                .uri("/api/v1/users")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponseModel.class)
                .hasSize(3);
    }

    @Test
    void whenRegisterStudent_thenReturnUserResponseModel() {
        StepVerifier.create(userRepository.findUsersByRolesContaining(Role.STUDENT))
                .expectNextCount(2)
                .verifyComplete();

        StudentRequestModel rq = StudentRequestModel.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe-ghost@null.local")
                .dob(LocalDate.parse("1995-01-01"))
                .address(Address.builder()
                        .streetAddress("456 Main St")
                        .city("Montreal")
                        .state("QC")
                        .zip("H0H 0H0")
                        .build()
                )
                .build();

        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .post()
                .uri("/api/v1/students")
                .bodyValue(rq)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponseModel.class);

        StepVerifier.create(userRepository.findUsersByRolesContaining(Role.STUDENT))
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void whenRegisterStudentWithExistingEmail_thenThrowEmailInUse(){
        StudentRequestModel rq = new StudentRequestModel();
        BeanUtils.copyProperties(student1, rq);
        rq.setFirstName("JaneOther");
        rq.setLastName("DoeOther");
        client.post()
                .uri("/api/v1/students")
                .bodyValue(rq)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(EmailInUse.class);
    }
    @Test
    void whenUpdateStudentRegistrationStatus_thenUpdateRegistrationStatus(){
        RegistrationStatusPatchRequestModel rq = RegistrationStatusPatchRequestModel.builder()
                .registrationStatus(RegistrationStatus.ACTIVE)
                .build();
        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .patch()
                .uri("/api/v1/students/7876ea26-3f76-4e50-870f-5e5dad6d63d2/registration-status")
                .bodyValue(rq)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StudentResponseModel.class)
                .value(student -> {
                    assertEquals("7876ea26-3f76-4e50-870f-5e5dad6d63d2", student.getUserId());
                    assertEquals("John", student.getFirstName());
                    assertEquals("Doe", student.getLastName());
                    assertEquals(RegistrationStatus.ACTIVE, student.getRegistrationStatus());
                });
    }
    @Test
    void whenUpdateNotPendingStudentRegistrationStatus_thenThrowStudentNotPending(){
        RegistrationStatusPatchRequestModel rq = RegistrationStatusPatchRequestModel.builder()
                .registrationStatus(RegistrationStatus.ACTIVE)
                .build();
        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .patch()
                .uri("/api/v1/students/7876ea26-3f76-4e50-870f-5e5dad6d63d1/registration-status")
                .bodyValue(rq)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody(StudentNotPending.class);
    }
    @Test
    void whenUpdateNonStudentRegistrationStatus_thenThrowNotFoundException(){
        String studentId = user1.getUserId();
        RegistrationStatusPatchRequestModel rq = RegistrationStatusPatchRequestModel.builder()
                .registrationStatus(RegistrationStatus.ACTIVE)
                .build();
        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .patch()
                .uri("/api/v1/students/{studentId}/registration-status", studentId)
                .bodyValue(rq)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Student with userId: " + studentId + " not found");
    }
    @Test
    void whenUpdateNotFoundStudentRegistrationStatus_thenThrowNotFoundException(){
        String userId = UUID.randomUUID().toString();
        RegistrationStatusPatchRequestModel rq = RegistrationStatusPatchRequestModel.builder()
                .registrationStatus(RegistrationStatus.ACTIVE)
                .build();
        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .patch()
                .uri("/api/v1/students/{studentId}/registration-status", userId)
                .bodyValue(rq)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("User with userId: " + userId + " not found");
    }
    @Test
    void whenUpdateStudentToInactive_thenDeleteStudent(){
        RegistrationStatusPatchRequestModel rq = RegistrationStatusPatchRequestModel.builder()
                .registrationStatus(RegistrationStatus.INACTIVE)
                .build();
        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .patch()
                .uri("/api/v1/students/{studentId}/registration-status", student2.getUserId())
                .bodyValue(rq)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StudentResponseModel.class)
                .value(student -> {
                    assertEquals(student2.getUserId(), student.getUserId());
                    assertEquals(student2.getFirstName(), student.getFirstName());
                    assertEquals(student2.getLastName(), student.getLastName());
                    assertEquals(RegistrationStatus.INACTIVE, student.getRegistrationStatus());
                });
        await().untilAsserted(() -> {
            StepVerifier.create(userRepository.findByUserId(student2.getUserId()))
                    .expectNextCount(0)
                    .verifyComplete();
        });
    }

    @Test
    public void whenRegisterStudentWithInvalidEmail_thenThrowInvalidInputException() {
        StudentRequestModel rq = StudentRequestModel.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe")
                .dob(LocalDate.parse("1995-01-01"))
                .address(Address.builder()
                        .streetAddress("456 Main St")
                        .city("Montreal")
                        .state("QC")
                        .zip("H0H 0H0")
                        .build()
                )
                .build();

        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .post()
                .uri("/api/v1/students")
                .bodyValue(rq)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(InvalidInputException.class);
    }

    @Test
    void whenGetAllStudentsByRegistrationStatuses_thenReturnStudentResponseModels() {


        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .get()
                .uri("/api/v1/students/status?statuses=PENDING")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponseModel.class)
                .hasSize(1)
                .consumeWith(response -> {
                    List<UserResponseModel> actualResponses = response.getResponseBody();
                    assertNotNull(actualResponses);
                    assertEquals(1, actualResponses.size());
                    assertEquals(student2.getUserId(), actualResponses.get(0).getUserId());
                    assertEquals(student2.getFirstName(), actualResponses.get(0).getFirstName());
                    assertEquals(student2.getLastName(), actualResponses.get(0).getLastName());
                    assertEquals(student2.getEmail(), actualResponses.get(0).getEmail());
                    assertEquals(student2.getDob(), actualResponses.get(0).getDob());
                    assertEquals(student2.getRoles(), actualResponses.get(0).getRoles());
                    assertEquals(student2.getAddress(), actualResponses.get(0).getAddress());
                });
    }

    @Test
    void whenGetCoursesByStudentIdWithValidStudent_ReturnCourses() {
        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .get()
                .uri("/api/v1/students/{studentId}/courses", student1.getUserId())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CourseResponseModel.class)
                .hasSize(1)
                .consumeWith(response -> {
                    List<CourseResponseModel> actualResponses = response.getResponseBody();
                    assertNotNull(actualResponses);
                    assertEquals(1, actualResponses.size());
                    assertEquals(course1.getCourseId(), actualResponses.get(0).getCourseId());
                    assertEquals(course1.getName(), actualResponses.get(0).getName());
                    assertEquals(course1.getInstructorId(), actualResponses.get(0).getInstructorId());
                    assertEquals(user1.getFirstName(), actualResponses.get(0).getInstructorFirstName());
                    assertEquals(user1.getLastName(), actualResponses.get(0).getInstructorLastName());
                    assertEquals(course1.getUserIds(), actualResponses.get(0).getUserIds());
                    assertEquals(course1.getCancelledDates(), actualResponses.get(0).getCancelledDates());
                    assertEquals(course1.getDayOfWeek(), actualResponses.get(0).getDayOfWeek());
                    assertEquals(course1.getStartTime(), actualResponses.get(0).getStartTime());
                    assertEquals(course1.getEndTime(), actualResponses.get(0).getEndTime());
                });
    }

    @Test
    void whenGetCoursesByStudentIdWithInvalidStudent_ReturnNotFound() {
        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .get()
                .uri("/api/v1/students/{studentId}/courses", "invalid")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenGetPendingStudentById_thenReturnStudentResponseModel() {
        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .get()
                .uri("/api/v1/students/pending/7876ea26-3f76-4e50-870f-5e5dad6d63d2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(StudentResponseModel.class)
                .value(student -> {
                    assertEquals("7876ea26-3f76-4e50-870f-5e5dad6d63d2", student.getUserId());
                    assertEquals("John", student.getFirstName());
                    assertEquals("Doe", student.getLastName());
                });
    }

    @Test
    void whenGetPendingStudentByInvalidId_thenThrowNotFoundException() {
        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .get()
                .uri("/api/v1/students/pending/7876ea26-3f76-4e50-870f-5e5dad6d63d1a")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(NotFoundException.class)
                .value(exception -> assertEquals(
                        "Pending student not found with userId: 7876ea26-3f76-4e50-870f-5e5dad6d63d1a",
                        exception.getMessage()
                ));
    }

    @Test
    void whenUpdateUserWithValidUserId_thenReturnUpdatedUserResponseModel() {
        UserResponseModel updatedUser = UserResponseModel.builder()
                .userId("97e64875-97b1-4ada-b370-6609b6e518ac")
                .firstName("UpdatedName")
                .lastName("Doe")
                .email("john.doe@null.local")
                .dob(LocalDate.parse("1990-01-01"))
                .roles(EnumSet.of(Role.CLIENT))
                .address(Address.builder()
                        .streetAddress("123 Main St")
                        .city("Montreal")
                        .state("QC")
                        .zip("H0H 0H0")
                        .build())
                .build();

        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .put()
                .uri("/api/v1/users/97e64875-97b1-4ada-b370-6609b6e518ac")
                .bodyValue(updatedUser)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseModel.class)
                .value(user -> {
                    assertEquals("97e64875-97b1-4ada-b370-6609b6e518ac", user.getUserId());
                    assertEquals("UpdatedName", user.getFirstName());
                    assertEquals("Doe", user.getLastName());
                });
    }

    @Test
    void whenGetUserByUserIdWithValidId_thenReturnUserResponseModel() {
        String userId = "7876ea26-3f76-4e50-870f-5e5dad6d63d1";
        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .get()
                .uri("/api/v1/users/{userId}", userId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseModel.class)
                .value(user -> {
                    assertEquals(userId, user.getUserId());
                    assertEquals(student1.getFirstName(), user.getFirstName());
                    assertEquals(student1.getLastName(), user.getLastName());
                });
    }

    @Test
    void whenGetUserByUserIdWithInvalidId_thenThrowNotFoundException() {
        String userId = "7876ea26-3f76-4e50-870f-5e5dad6d63d1o";
        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .get()
                .uri("/api/v1/users/{userId}", userId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(NotFoundException.class)
                .value(exception -> assertEquals("User with userId: " + userId + " not found", exception.getMessage()));
    }

    @Test
    void whenGetStudentByIdWithValidId_thenReturnStudentResponseModel() {
        String studentId = "7876ea26-3f76-4e50-870f-5e5dad6d63d1";
        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .get()
                .uri("/api/v1/students/{studentId}", studentId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseModel.class)
                .value(student -> {
                    assertEquals(studentId, student.getUserId());
                    assertEquals(student1.getFirstName(), student.getFirstName());
                    assertEquals(student1.getLastName(), student.getLastName());
                    assertEquals(student1.getEmail(), student.getEmail());
                    assertEquals(student1.getDob(), student.getDob());
                    assertEquals(student1.getRoles(), student.getRoles());
                    assertEquals(student1.getAddress(), student.getAddress());
                });
    }

    @Test
    void whenGetStudentByIdWithInvalidId_thenThrowNotFoundException() {
        String invalidStudentId = "7876ea26-3f76-4e50-870f-5e5dad6d63da";
        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .get()
                .uri("/api/v1/students/{studentId}", invalidStudentId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(NotFoundException.class)
                .value(exception -> assertEquals(
                        "Student not found with studentId: " + invalidStudentId,
                        exception.getMessage()
                ));
    }

    @Test
    void whenUpdateStudentDetails_thenUpdateStudent() {
        StudentRequestModel rq = StudentRequestModel.builder()
                .firstName("UpdatedFirstName")
                .lastName("UpdatedLastName")
                .email("updated.email@null.local")
                .dob(LocalDate.parse("1995-01-01"))
                .address(Address.builder()
                        .streetAddress("456 Updated St")
                        .city("UpdatedCity")
                        .state("QC")
                        .zip("H0H 0H0")
                        .build())
                .build();

        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .put()
                .uri("/api/v1/students/{studentId}", student1.getUserId())
                .bodyValue(rq)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StudentResponseModel.class)
                .value(student -> {
                    assertEquals(student1.getUserId(), student.getUserId());
                    assertEquals("UpdatedFirstName", student.getFirstName());
                    assertEquals("UpdatedLastName", student.getLastName());
                    assertEquals("updated.email@null.local", student.getEmail());
                    assertEquals(LocalDate.parse("1995-01-01"), student.getDob());
                    assertEquals("456 Updated St", student.getAddress().getStreetAddress());
                    assertEquals("UpdatedCity", student.getAddress().getCity());
                    assertEquals("QC", student.getAddress().getState());
                    assertEquals("H0H 0H0", student.getAddress().getZip());
                });
    }

    @Test
    void whenUpdateStudentDetailsWithInvalidId_thenThrowNotFoundException() {
        String userId = "invalid-user-id";
        StudentRequestModel rq = StudentRequestModel.builder()
                .firstName("UpdatedFirstName")
                .lastName("UpdatedLastName")
                .email("updated.email@null.local")
                .dob(LocalDate.parse("1995-01-01"))
                .address(Address.builder()
                        .streetAddress("456 Updated St")
                        .city("UpdatedCity")
                        .state("QC")
                        .zip("H0H 0H0")
                        .build())
                .build();

        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .put()
                .uri("/api/v1/students/{studentId}", userId)
                .bodyValue(rq)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Student not found with userId: " + userId);
    }
    @Test
    void whenUpdateUserRole_thenUserRoleIsUpdated() {
        UserRolePatchRequestModel rolePatchRequest = UserRolePatchRequestModel.builder()
                .roles(Collections.singletonList(Role.ADMIN))
                .build();

        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .patch()
                .uri("/api/v1/users/" + user1.getUserId() + "/role")
                .bodyValue(rolePatchRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseModel.class)
                .value(response -> {
                    assertEquals(user1.getUserId(), response.getUserId());
                    assertTrue(response.getRoles().contains(Role.ADMIN));
                });

        StepVerifier.create(userRepository.findUserByUserId(user1.getUserId()))
                .assertNext(user -> assertTrue(user.getRoles().contains(Role.ADMIN)))
                .verifyComplete();
    }

    @Test
    void whenUpdateUserRoleForNonExistentUser_thenReturnNotFound() {
        UserRolePatchRequestModel rolePatchRequest = UserRolePatchRequestModel.builder()
                .roles(Collections.singletonList(Role.ADMIN))
                .build();

        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .patch()
                .uri("/api/v1/users/nonexistent-user-id/role")
                .bodyValue(rolePatchRequest)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(errorMessage -> {
                    assertTrue(errorMessage.contains("User with userId: nonexistent-user-id not found"));
                });
    }
    @Test
    void whenAddNewUserWithValidData_thenReturnUserResponseModel() {
        UserRequestModel newUserRequest = UserRequestModel.builder()
                .firstName("Alice")
                .lastName("Smith")
                .email("alice.smith@null.local")
                .dob(LocalDate.parse("1995-01-01"))
                .address(Address.builder()
                        .streetAddress("789 Elm St")
                        .city("Toronto")
                        .state("ON")
                        .zip("M4B 1B3")
                        .build())
                .phone("123-456-7890")
                .build();

        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .post()
                .uri("/api/v1/users?role=CLIENT")
                .bodyValue(newUserRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseModel.class)
                .value(response -> {
                    assertNotNull(response.getUserId());
                    assertEquals("Alice", response.getFirstName());
                    assertEquals("Smith", response.getLastName());
                    assertEquals("CLIENT", response.getRoles().iterator().next().name());
                });
    }

    @Test
    void whenAddNewUserWithInvalidEmail_thenThrowInvalidInputException() {
        UserRequestModel newUserRequest = UserRequestModel.builder()
                .firstName("Alice")
                .lastName("Smith")
                .email("invalid-email")
                .dob(LocalDate.parse("1995-01-01"))
                .address(Address.builder()
                        .streetAddress("789 Elm St")
                        .city("Toronto")
                        .state("ON")
                        .zip("M4B 1B3")
                        .build())
                .phone("123-456-7890")
                .build();

        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .post()
                .uri("/api/v1/users?role=CLIENT")
                .bodyValue(newUserRequest)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(InvalidInputException.class);
    }

    @Test
    void subscribeToPromotionsWithValidRequest() {
        userRepository.save(testUser).block();

        Map<String, String> requestBody = Map.of("isSubscribed", "true");

        client.patch()
                .uri("/api/v1/users/test-user-id/subscription")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseModel.class)
                .value(response -> assertThat(response.getIsSubscribed()).isTrue());
    }

    @Test
    void unsubscribeFromPromotionsWithValidRequest() {
        userRepository.save(testUser).block();

        Map<String, String> requestBody = Map.of("isSubscribed", "false");

        client.patch()
                .uri("/api/v1/users/test-user-id/subscription")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseModel.class)
                .value(response -> assertThat(response.getIsSubscribed()).isFalse());
    }
    @Test
    void whenSubscribeToPromotionsWithNullIsSubscribed_thenThrowIllegalArgumentException() {
        userRepository.save(testUser).block();

        Map<String, String> requestBody = Map.of("isSubscribed", "");

        client.patch()
                .uri("/api/v1/users/test-user-id/subscription")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.message").isEqualTo("isSubscribed cannot be null or empty");
    }


    @Test
    void whenLinkUserAccountWithValidId_thenReturnUpdatedUserResponseModel() {
        String userId = user1.getUserId();

        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .patch()
                .uri("/api/v1/users/{userId}/link-account", userId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseModel.class)
                .value(response -> {
                    assertEquals(userId, response.getUserId());
                    assertEquals(staff.getAssociatedId(), response.getAssociatedId());
                    assertEquals(user1.getFirstName(), response.getFirstName());
                    assertEquals(user1.getLastName(), response.getLastName());
                });

        StepVerifier.create(userRepository.findUserByUserId(userId))
                .assertNext(user -> {
                    assertEquals(staff.getAssociatedId(), user.getAssociatedId());
                })
                .verifyComplete();
    }

    @Test
    void whenLinkUserAccountWithInvalidId_thenReturnNotFound() {
        String invalidUserId = "nonexistent-user-id";

        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .patch()
                .uri("/api/v1/users/{userId}/link-account", invalidUserId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("User with userId: " + invalidUserId + " not found");
    }

    @Test
    void whenLinkUserAccountWithAlreadyLinkedAccount_thenReturnConflict() {
        String userId = user1.getUserId();
        String existingAssociatedId = "existingAssociatedId";

        StepVerifier.create(userRepository.findUserByUserId(userId)
                .flatMap(user -> {
                    user.setAssociatedId(existingAssociatedId);
                    return userRepository.save(user);
                }))
                .expectNextCount(1)
                .verifyComplete();

        client
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .patch()
                .uri("/api/v1/users/{userId}/link-account", userId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Account is already linked");
    }


}
