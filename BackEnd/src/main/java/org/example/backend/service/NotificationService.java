package org.example.backend.service;

import org.example.backend.dto.NotificationRequest;
import org.example.backend.dto.NotificationResponse;
import org.example.backend.entity.Notification;
import org.example.backend.entity.Account;
import org.example.backend.repository.NotificationRepository;
import org.example.backend.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<NotificationResponse> getMyNotifications() {
        Account account = getCurrentAccount();
        return notificationRepository.findByAccountOrderByCreatedAtDesc(account).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public long getUnreadCount() {
        Account account = getCurrentAccount();
        return notificationRepository.countByAccountAndIsReadFalse(account);
    }

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

    public void markAllAsRead() {
        Account account = getCurrentAccount();
        notificationRepository.markAllAsReadForAccount(account);
    }

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
