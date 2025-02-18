package com.liondance.liondance_backend.logiclayer.Notification;

import com.liondance.liondance_backend.datalayer.Notification.Notification;
import com.liondance.liondance_backend.datalayer.Notification.NotificationRepository;
import com.liondance.liondance_backend.datalayer.Notification.NotificationStatus;
import com.liondance.liondance_backend.datalayer.Notification.NotificationType;
import com.liondance.liondance_backend.presentationlayer.Notification.NotificationResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final MailSender mailSender;
    private final SimpleMailMessage templateMessage;

    public NotificationServiceImpl(
            MailSender mailSender,
            @Value("${MAIL_SMTP_USER}@${SERVER_ROOT_DOMAIN}") String from,
            NotificationRepository notificationRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        this.templateMessage = msg;
    }

    public Flux<NotificationResponseModel> getAllNotifications() {
        return notificationRepository.findAll().map(NotificationResponseModel::from);
    }

    public boolean sendMail(String to, String subject, String body, NotificationType notificationType) {
        Notification notification = Notification.builder()
                .notificationId(UUID.randomUUID().toString())
                .type(notificationType)
                .status(NotificationStatus.PENDING)
                .recipient(to)
                .sentAt(Instant.now())
                .build();
        log.debug(notification.getSentAt().toString());
        SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        notificationRepository.save(notification).subscribe();
        try{
            mailSender.send(msg);
            log.info("Email sent to {}", to);
            notification.setStatus(NotificationStatus.SENT);
            notificationRepository.save(notification).subscribe();
            return true;
        } catch (MailException e) {
            notification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification).subscribe();
            log.warn("Failed to send email to {}", to, e);
            return false;
        }
    }

    public boolean sendMail(String to, String subject, String body, NotificationType notificationType, String associatedId) {
        Notification notification = Notification.builder()
                .notificationId(UUID.randomUUID().toString())
                .type(notificationType)
                .status(NotificationStatus.PENDING)
                .recipient(to)
                .sentAt(Instant.now())
                .build();
        log.debug(notification.getSentAt().toString());
        SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        notificationRepository.save(notification).subscribe();
        try{
            mailSender.send(msg);
            log.info("Email sent to {}", to);
            notification.setStatus(NotificationStatus.SENT);
            notification.setAssociatedId(associatedId);
            notificationRepository.save(notification).subscribe();
            return true;
        } catch (MailException e) {
            notification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification).subscribe();
            log.warn("Failed to send email to {}", to, e);
            return false;
        }
    }

    @Override
    public Mono<Notification> getNotificationByAssociatedId(String associatedId, NotificationType notificationType) {
        return notificationRepository.getNotificationByTypeAndAssociatedId(notificationType, associatedId);
    }
}
