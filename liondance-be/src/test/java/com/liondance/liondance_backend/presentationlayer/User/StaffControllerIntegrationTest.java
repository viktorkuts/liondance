package com.liondance.liondance_backend.presentationlayer.User;

import com.liondance.liondance_backend.datalayer.User.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.data.mongodb.port= 0"})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
class StaffControllerIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebTestClient client;

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
            .joinDate(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
            .registrationStatus(RegistrationStatus.ACTIVE)
            .build();

    Student student2 = Student.builder()
            .userId("7876ea26-3f76-4e50-870f-5e5dad6d63d1")
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@null.local")
            .dob(LocalDate.parse("2000-01-01"))
            .gender(Gender.MALE)
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
    public void SetupDB(){
    Publisher<User> UserPublisher = userRepository.deleteAll()
            .thenMany(
                Flux.just(student1, student2)
                    .flatMap(userRepository::save)
            );
    StepVerifier.create(UserPublisher).expectNextCount(2).verifyComplete();
}

    @Test
    void whenGetAllStudentsByRegistrationStatuses_thenReturnStudentResponseModels() {
        UserResponseModel expectedResponse = UserResponseModel.from(student2);

        client.get()
                .uri("/api/v1/staff/registrations?statuses=PENDING")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponseModel.class)
                .hasSize(1)
                .consumeWith(response -> {
                    List<UserResponseModel> actualResponses = response.getResponseBody();
                    assertNotNull(actualResponses);
                    assertEquals(1, actualResponses.size());
                    assertEquals(expectedResponse, actualResponses.get(0));
                });
    }

}