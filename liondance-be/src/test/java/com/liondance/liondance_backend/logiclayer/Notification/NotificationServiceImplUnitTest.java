package com.liondance.liondance_backend.logiclayer.Notification;

import com.liondance.liondance_backend.datalayer.Notification.Notification;
import com.liondance.liondance_backend.datalayer.Notification.NotificationRepository;
import com.liondance.liondance_backend.datalayer.Notification.NotificationStatus;
import com.liondance.liondance_backend.datalayer.Notification.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationServiceImplUnitTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private MailSender mailSender;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_SUBJECT = "Test Subject";
    private static final String TEST_MESSAGE = "Test Message";
    private static final String FROM_EMAIL = "from@test.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SimpleMailMessage templateMessage = new SimpleMailMessage();
        templateMessage.setFrom("test@test.com");
        ReflectionTestUtils.setField(notificationService, "templateMessage", templateMessage);
    }

    @Test
    void sendMail_Success() {
        // Arrange
        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(Mono.just(Notification.builder().build()));
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        boolean result = notificationService.sendMail(
                TEST_EMAIL,
                TEST_SUBJECT,
                TEST_MESSAGE,
                NotificationType.ADMIN_MESSAGE
        );

        // Assert
        assertTrue(result);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void sendMail_FailureToSend() {
        // Arrange
        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(Mono.just(Notification.builder().build()));
        doThrow(new MailException("Mail send failed") {})
                .when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        boolean result = notificationService.sendMail(
                TEST_EMAIL,
                TEST_SUBJECT,
                TEST_MESSAGE,
                NotificationType.ADMIN_MESSAGE
        );

        // Assert
        assertFalse(result);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void sendMail_VerifyNotificationContent() {
        // Arrange
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> {
                    Notification notification = invocation.getArgument(0);
                    assertEquals(NotificationType.ADMIN_MESSAGE, notification.getType());
                    assertEquals(TEST_EMAIL, notification.getRecipient());
                    assertNotNull(notification.getNotificationId());
                    assertNotNull(notification.getSentAt());
                    return Mono.just(notification);
                });

        // Act
        notificationService.sendMail(
                TEST_EMAIL,
                TEST_SUBJECT,
                TEST_MESSAGE,
                NotificationType.ADMIN_MESSAGE
        );

        // Assert
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void sendMail_VerifyStatusUpdates() {
        // Arrange
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> {
                    Notification notification = invocation.getArgument(0);
                    if (notification.getStatus() == NotificationStatus.PENDING) {
                        return Mono.just(notification);
                    } else {
                        assertEquals(NotificationStatus.SENT, notification.getStatus());
                        return Mono.just(notification);
                    }
                });

        // Act
        notificationService.sendMail(
                TEST_EMAIL,
                TEST_SUBJECT,
                TEST_MESSAGE,
                NotificationType.ADMIN_MESSAGE
        );

        // Assert
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }
} 