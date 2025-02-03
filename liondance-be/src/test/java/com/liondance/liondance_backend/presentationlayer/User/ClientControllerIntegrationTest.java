package com.liondance.liondance_backend.presentationlayer.User;

import com.liondance.liondance_backend.datalayer.User.Client;
import com.liondance.liondance_backend.datalayer.User.Role;
import com.liondance.liondance_backend.datalayer.User.User;
import com.liondance.liondance_backend.datalayer.User.UserRepository;
import com.liondance.liondance_backend.datalayer.common.Address;
import com.liondance.liondance_backend.logiclayer.User.UserService;
import com.liondance.liondance_backend.utils.WebTestAuthConfig;
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

import java.util.EnumSet;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.data.mongodb.port= 0"})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
public class ClientControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private UserRepository userRepository;

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
            .roles(EnumSet.of(Role.STAFF))
            .associatedId("thetesterstaff")
            .build();

    private final User client1 = User.builder()
            .userId("c56a8e9d-4362-42c8-965d-2b8b98f9f4d9")
            .firstName("Alice")
            .lastName("Johnson")
            .email("alice.johnson@webmail.com")
            .roles(EnumSet.of(Role.CLIENT))
            .build();

    private final User client2 = User.builder()
            .userId("d89f1a3e-01a5-4f97-b2a3-9927555e4951")
            .firstName("Bob")
            .email("bob.lee@someplace.com")
            .roles(EnumSet.of(Role.CLIENT))
            .build();

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setupDB() {
        Publisher<User> setupDB = userRepository.deleteAll()
                .thenMany(Flux.just(client1, client2))
                .flatMap(userRepository::save);

        StepVerifier.create(setupDB).expectNextCount(2).verifyComplete();
    }

    @Test
    void getAllClients_returnsClients_whenClientsExist() {

        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .get()
                .uri("/api/v1/clients")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponseModel.class)
                .hasSize(2);

        StepVerifier.create(userRepository.findAll())
                .expectNextMatches(client -> client.getUserId().equals(client1.getUserId()))
                .expectNextMatches(client -> client.getUserId().equals(client2.getUserId()))
                .verifyComplete();
    }
}
