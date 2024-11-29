package com.liondance.liondance_backend.datalayer.Notification;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface NotificationRepository extends ReactiveMongoRepository<Notification, String> {

}
