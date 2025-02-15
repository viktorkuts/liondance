package com.liondance.liondance_backend.presentationlayer.Notification;

import com.liondance.liondance_backend.logiclayer.Notification.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.liondance.liondance_backend.datalayer.Notification.NotificationType;

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

    @PreAuthorize("hasAuthority('STAFF')")
    @PostMapping("/contact-client")
    public Mono<ResponseEntity<Void>> contactClient(@RequestBody ContactClientRequest request) {
        return Mono.just(notificationService.sendMail(
                request.getEmail(),
                "LVH Lion Dance Team - New Message",
                request.getMessage(),
                NotificationType.ADMIN_MESSAGE
        ))
        .map(sent -> sent ? 
            ResponseEntity.ok().<Void>build() : 
            ResponseEntity.badRequest().<Void>build()
        );
    }
}
