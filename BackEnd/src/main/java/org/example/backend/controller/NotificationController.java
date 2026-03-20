package org.example.backend.controller;

import org.example.backend.dto.NotificationRequest;
import org.example.backend.dto.NotificationResponse;
import org.example.backend.service.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final INotificationService notificationService;

    /**
     * Endpoint lấy danh sách thông báo của người dùng đang đăng nhập.
     * 
     * @return ResponseEntity chứa danh sách các thông báo cá nhân.
     */
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications() {
        return ResponseEntity.ok(notificationService.getMyNotifications());
    }

    /**
     * Endpoint lấy số lượng thông báo chưa đọc.
     * 
     * @return ResponseEntity chứa số lượng thông báo chưa đọc.
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        return ResponseEntity.ok(notificationService.getUnreadCount());
    }

    /**
     * Endpoint đánh dấu một thông báo là đã đọc.
     * 
     * @param id ID của thông báo cần đánh dấu.
     * @return ResponseEntity xác nhận thao tác thành công.
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint đánh dấu tất cả thông báo của người dùng là đã đọc.
     * 
     * @return ResponseEntity xác nhận thao tác thành công.
     */
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint tạo thông báo mới. 
     * Cho quyền MENTOR hoặc ADMIN để gửi thông báo cho sinh viên.
     * 
     * @param request Thông tin cần thiết để tạo thông báo.
     * @return ResponseEntity xác nhận thao tác thành công.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN')")
    public ResponseEntity<?> createNotification(@RequestBody NotificationRequest request) {
        notificationService.createNotification(request);
        return ResponseEntity.ok().build();
    }
}
