package com.liondance.liondance_backend.presentationlayer.Notification;

import com.liondance.liondance_backend.datalayer.Notification.Notification;
import com.liondance.liondance_backend.datalayer.Notification.NotificationStatus;
import com.liondance.liondance_backend.datalayer.Notification.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseModel {
    private String notificationId;
    private String recipient;
    private Instant sentAt;
    private NotificationType type;
    private NotificationStatus status;

    public static NotificationResponseModel from(Notification notification) {
        NotificationResponseModel responseModel = new NotificationResponseModel();
        BeanUtils.copyProperties(notification, responseModel);
        return responseModel;
    }
}
