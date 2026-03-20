package org.example.backend.service.impl;

import org.example.backend.dto.NotificationRequest;
import org.example.backend.dto.NotificationResponse;
import org.example.backend.entity.Notification;
import org.example.backend.entity.Account;
import org.example.backend.repository.NotificationRepository;
import org.example.backend.repository.AccountRepository;
import org.example.backend.service.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService implements INotificationService {

    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;

    /**
     * Lấy danh sách thông báo của người dùng hiện tại đang đăng nhập.
     * Sắp xếp theo thời gian tạo giảm dần.
     * 
     * @return List<NotificationResponse> Danh sách các thông báo của người dùng.
     */
    public List<NotificationResponse> getMyNotifications() {
        Account account = getCurrentAccount();
        return notificationRepository.findByAccountOrderByCreatedAtDesc(account).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Đếm số lượng thông báo chưa đọc của người dùng hiện tại.
     * 
     * @return long Số lượng thông báo chưa đọc.
     */
    public long getUnreadCount() {
        Account account = getCurrentAccount();
        return notificationRepository.countByAccountAndIsReadFalse(account);
    }

    /**
     * Đánh dấu một thông báo cụ thể là đã đọc.
     * Kiểm tra quyền sở hữu thông báo trước khi cập nhật.
     * 
     * @param id ID của thông báo cần đánh dấu.
     */
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        // Ensure user owns this notification
        Account currentAccount = getCurrentAccount();
        if (!notification.getAccount().getId().equals(currentAccount.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    /**
     * Đánh dấu tất cả thông báo của người dùng hiện tại là đã đọc.
     */
    public void markAllAsRead() {
        Account account = getCurrentAccount();
        notificationRepository.markAllAsReadForAccount(account);
    }

    /**
     * Tạo thông báo gửi đến các tài khoản được chỉ định hoặc toàn bộ thực tập sinh.
     * Nếu danh sách ID tài khoản trống, thông báo sẽ gửi đến tất cả thực tập sinh.
     * 
     * @param request Chứa thông tin tiêu đề, nội dung và danh sách ID tài khoản nhận.
     */
    public void createNotification(NotificationRequest request) {
        List<Account> targetAccounts;
        if (request.getAccountIds() != null && !request.getAccountIds().isEmpty()) {
            targetAccounts = accountRepository.findAllById(request.getAccountIds());
        } else {
            targetAccounts = accountRepository.findAll().stream()
                    .filter(a -> a.getRole() == Account.Role.USER)
                    .collect(Collectors.toList());
        }

        List<Notification> notifications = targetAccounts.stream()
                .map(account -> Notification.builder()
                        .title(request.getTitle())
                        .content(request.getContent())
                        .account(account)
                        .createdAt(LocalDateTime.now())
                        .isRead(false)
                        .build())
                .collect(Collectors.toList());

        notificationRepository.saveAll(notifications);
    }

    private Account getCurrentAccount() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .createdAt(notification.getCreatedAt())
                .isRead(notification.isRead())
                .build();
    }
}
