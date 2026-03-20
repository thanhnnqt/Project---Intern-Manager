package org.example.backend.service;

import org.example.backend.dto.NotificationRequest;
import org.example.backend.dto.NotificationResponse;
import java.util.List;

public interface INotificationService {
    List<NotificationResponse> getMyNotifications();
    long getUnreadCount();
    void markAsRead(Long id);
    void markAllAsRead();
    void createNotification(NotificationRequest request);
}
