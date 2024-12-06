package com.liondance.liondance_backend.presentationlayer.User;

import com.liondance.liondance_backend.datalayer.User.*;
import com.liondance.liondance_backend.logiclayer.User.UserService;
import com.liondance.liondance_backend.utils.exceptions.InvalidInputException;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.LocalDate;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.data.mongodb.port= 0"})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
class UserControllerIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebTestClient client;

    User user1 = User.builder()
            .userId("97e64875-97b1-4ada-b370-6609b6e518ac")
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@null.local")
            .dob(LocalDate.parse("1990-01-01"))
            .gender(Gender.MALE)
            .roles(EnumSet.of(Role.CLIENT))
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
            .gender(Gender.FEMALE)
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

    @BeforeEach
    public void setupDB(){
        Publisher<User> userPublisher = userRepository.deleteAll()
                .thenMany(
                        Flux.just(user1, student1)
                                .flatMap(userRepository::save)
                );
        StepVerifier.create(userPublisher).expectNextCount(2).verifyComplete();
    }

    @Test
    void whenGetAllUsers_thenReturnUserResponseModels() {
        client.get()
                .uri("/api/v1/users")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponseModel.class)
                .hasSize(2);
    }

    @Test
    void whenGetAllStudents_thenReturnStudentResponseModels(){
        client.get()
                .uri("/api/v1/students")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponseModel.class)
                .hasSize(1);
    }

    @Test
    void whenRegisterStudent_thenReturnUserResponseModel() {
        StepVerifier.create(userRepository.findUsersByRolesContaining(Role.STUDENT))
                .expectNextCount(1)
                .verifyComplete();

        StudentRequestModel rq = StudentRequestModel.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@null.local")
                .dob(LocalDate.parse("1995-01-01"))
                .address(Address.builder()
                        .streetAddress("456 Main St")
                        .city("Montreal")
                        .state("QC")
                        .zip("H0H 0H0")
                        .build()
                )
                .gender(Gender.FEMALE)
                .build();

        client.post()
                .uri("/api/v1/students")
                .bodyValue(rq)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponseModel.class);

        StepVerifier.create(userRepository.findUsersByRolesContaining(Role.STUDENT))
                .expectNextCount(2)
                .verifyComplete();
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

        client.post()
                .uri("/api/v1/students")
                .bodyValue(rq)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(InvalidInputException.class);
    }

    @Test
    void whenGetUserByUserId_thenReturnUserResponseModel() {
        client.get()
                .uri("/api/v1/users/97e64875-97b1-4ada-b370-6609b6e518ac")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseModel.class)
                .value(user -> {
                    assertEquals("97e64875-97b1-4ada-b370-6609b6e518ac", user.getUserId());
                    assertEquals("John", user.getFirstName());
                    assertEquals("Doe", user.getLastName());
                });
    }

    @Test
    void whenGetUserByInvalidUserId_thenThrowNotFoundException() {
        client.get()
                .uri("/api/v1/users/97e64875-97b1-4ada-b370-6609b6e518aca")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(NotFoundException.class)
                .value(exception -> assertEquals(
                        "User with userId: 97e64875-97b1-4ada-b370-6609b6e518aca not found",
                        exception.getMessage()
                ));
    }

}