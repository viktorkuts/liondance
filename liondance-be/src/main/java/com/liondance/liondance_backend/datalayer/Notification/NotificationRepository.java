package com.liondance.liondance_backend.datalayer.Notification;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface NotificationRepository extends ReactiveMongoRepository<Notification, String> {

    Mono<Notification> getNotificationByAssociatedId(String associatedId);
    Mono<Notification> getNotificationByTypeAndAssociatedId(NotificationType type, String associatedId);
}
