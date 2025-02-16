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
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.data.mongodb.port= 0"})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
public class ClientControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private UserRepository userRepository;
    @SpyBean
    private UserService userService;



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
            .associatedId("thetester1")
            .build();

    private final User client2 = User.builder()
            .userId("d89f1a3e-01a5-4f97-b2a3-9927555e4951")
            .firstName("Bob")
            .email("bob.lee@someplace.com")
            .roles(EnumSet.of(Role.CLIENT))
            .associatedId("thetester2")
            .build();

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

    @Test
    @WithMockUser(authorities = "STAFF")
    void getClientDetails_withValidClientId_shouldReturnClientDetails() {
        String clientId = "c56a8e9d-4362-42c8-965d-2b8b98f9f4d9";
        ClientResponseModel mockClient = ClientResponseModel.builder()
                .userId(clientId)
                .firstName("Alice")
                .lastName("Johnson")
                .email("alice.johnson@webmail.com")
                .phone("234-567-8901")
                .roles(EnumSet.of(Role.CLIENT))
                .activeEvents(Collections.emptyList())
                .pastEvents(Collections.emptyList())
                .build();

        when(userService.getClientDetails(clientId)).thenReturn(Mono.just(mockClient));

        webTestClient.get()
                .uri("/api/v1/clients/" + clientId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClientResponseModel.class)
                .value(response -> {
                    assertEquals(clientId, response.getUserId());
                    assertEquals("Alice", response.getFirstName());
                    assertEquals("Johnson", response.getLastName());
                    assertEquals("alice.johnson@webmail.com", response.getEmail());
                    assertEquals("234-567-8901", response.getPhone());
                });

        verify(userService, times(1)).getClientDetails(clientId);
    }

    @Test
    void addClient_thenReturnAddedClient() {
        UserRequestModel notRelatedRequest = UserRequestModel.builder()
                .firstName("UnrelatedGuy")
                .lastName("UnrelatedFamily")
                .email("helloIamnobody@example.com")
                .dob(LocalDate.now())
                .phone("1234567890")
                .build();
        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .post()
                .uri("/api/v1/clients")
                .bodyValue(notRelatedRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.firstName").isEqualTo(notRelatedRequest.getFirstName())
                .jsonPath("$.lastName").isEqualTo(notRelatedRequest.getLastName())
                .jsonPath("$.email").isEqualTo(notRelatedRequest.getEmail());
        StepVerifier.create(userRepository.findAll())
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void addClientExists_thenReturnsClient() {
        userRepository.deleteAll().block();

        userRepository.save(client1).block();
        userRepository.save(client2).block();

        UserRequestModel notRelatedRequest = UserRequestModel.builder()
                .firstName("UnrelatedGuy")
                .lastName("UnrelatedFamily")
                .email("helloIamnobody@example.com")
                .dob(LocalDate.now())
                .phone("1234567890")
                .build();

        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(client1))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .post()
                .uri("/api/v1/clients")
                .bodyValue(notRelatedRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.firstName").isEqualTo(client1.getFirstName())
                .jsonPath("$.lastName").isEqualTo(client1.getLastName())
                .jsonPath("$.email").isEqualTo(client1.getEmail());

        StepVerifier.create(userRepository.findAll())
                .expectNextMatches(user -> user.getUserId().equals(client1.getUserId()))
                .expectNextMatches(user -> user.getUserId().equals(client2.getUserId()))
                .verifyComplete();
    }
}
