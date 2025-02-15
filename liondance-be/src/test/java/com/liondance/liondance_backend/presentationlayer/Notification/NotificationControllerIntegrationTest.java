package com.liondance.liondance_backend.presentationlayer.Notification;

import com.liondance.liondance_backend.datalayer.Notification.NotificationType;
import com.liondance.liondance_backend.datalayer.User.Role;
import com.liondance.liondance_backend.logiclayer.Notification.NotificationService;
import com.liondance.liondance_backend.datalayer.User.User;
import com.liondance.liondance_backend.utils.WebTestAuthConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.EnumSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureWebTestClient
class NotificationControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private NotificationService notificationService;

    private static final String BASE_URI = "/api/v1/notifications";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_MESSAGE = "Test message content";

    private final User staff = User.builder()
            .userId("staff-1")
            .email("staff@test.com")
            .roles(EnumSet.of(Role.STAFF))
            .build();

    private final User user = User.builder()
            .userId("user-1")
            .email("user@test.com")
            .roles(EnumSet.of(Role.CLIENT))
            .build();

    @Test
    void contactClient_Success() {
        // Arrange
        ContactClientRequest request = new ContactClientRequest(TEST_EMAIL, TEST_MESSAGE);
        when(notificationService.sendMail(
                eq(TEST_EMAIL),
                any(String.class),
                eq(TEST_MESSAGE),
                eq(NotificationType.ADMIN_MESSAGE)
        )).thenReturn(true);

        // Act & Assert
        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .post()
                .uri(BASE_URI + "/contact-client")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void contactClient_FailureToSend() {
        // Arrange
        ContactClientRequest request = new ContactClientRequest(TEST_EMAIL, TEST_MESSAGE);
        when(notificationService.sendMail(
                eq(TEST_EMAIL),
                any(String.class),
                eq(TEST_MESSAGE),
                eq(NotificationType.ADMIN_MESSAGE)
        )).thenReturn(false);

        // Act & Assert
        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .post()
                .uri(BASE_URI + "/contact-client")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void contactClient_InvalidEmail() {
        // Arrange
        ContactClientRequest request = new ContactClientRequest("invalid-email", TEST_MESSAGE);

        // Act & Assert
        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .post()
                .uri(BASE_URI + "/contact-client")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void contactClient_EmptyMessage() {
        // Arrange
        ContactClientRequest request = new ContactClientRequest(TEST_EMAIL, "");

        // Act & Assert
        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .post()
                .uri(BASE_URI + "/contact-client")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void contactClient_UnauthorizedUser() {
        // Arrange
        ContactClientRequest request = new ContactClientRequest(TEST_EMAIL, TEST_MESSAGE);

        // Act & Assert
        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(user))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .post()
                .uri(BASE_URI + "/contact-client")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isForbidden();
    }
} 