package com.liondance.liondance_backend.logiclayer.Notification;

import com.liondance.liondance_backend.datalayer.Notification.NotificationType;
import com.liondance.liondance_backend.presentationlayer.Notification.NotificationResponseModel;
import reactor.core.publisher.Flux;

public interface NotificationService {
    Flux<NotificationResponseModel> getAllNotifications();
    boolean sendMail(String to, String subject, String body, NotificationType notificationType);
}
