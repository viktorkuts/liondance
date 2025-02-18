package com.liondance.liondance_backend.logiclayer.Notification;

import com.liondance.liondance_backend.datalayer.Notification.Notification;
import com.liondance.liondance_backend.datalayer.Notification.NotificationType;
import com.liondance.liondance_backend.presentationlayer.Notification.NotificationResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationService {
    Flux<NotificationResponseModel> getAllNotifications();
    boolean sendMail(String to, String subject, String body, NotificationType notificationType);
    boolean sendMail(String to, String subject, String body, NotificationType notificationType, String associatedId);
    Mono<Notification> getNotificationByAssociatedId(String associatedId, NotificationType notificationType);
}
