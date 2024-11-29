package com.liondance.liondance_backend.presentationlayer.Notification;

import com.liondance.liondance_backend.logiclayer.Notification.NotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public Flux<NotificationResponseModel> getAllNotifications() {
        return notificationService.getAllNotifications();
    }
}
