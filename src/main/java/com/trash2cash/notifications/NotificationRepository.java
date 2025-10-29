package com.trash2cash.notifications;

import com.trash2cash.users.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    List<Notification> findByUserIdAndReadStatusFalse(Long userId);
    Long countByUserIdAndReadStatusFalse(Long userId);
    Page<Notification> findBySenderIdOrderByCreatedAtDesc(Long senderId, Pageable pageable);

    void deleteByUser(User user);
    void deleteBySender(User sender);

}
