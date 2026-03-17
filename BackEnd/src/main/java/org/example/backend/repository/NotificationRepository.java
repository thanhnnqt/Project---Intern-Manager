package org.example.backend.repository;

import org.example.backend.entity.Notification;
import org.example.backend.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByAccountOrderByCreatedAtDesc(Account account);
    long countByAccountAndIsReadFalse(Account account);
    
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.account = :account AND n.isRead = false")
    void markAllAsReadForAccount(Account account);
}
