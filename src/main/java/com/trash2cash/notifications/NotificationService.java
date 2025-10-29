package com.trash2cash.notifications;

import com.trash2cash.dto.AnnouncementResponse;
import com.trash2cash.firebase.FCMService;
import com.trash2cash.users.enums.NotificationScope;
import com.trash2cash.users.enums.UserRole;
import com.trash2cash.users.repo.DeviceRepository;
import com.trash2cash.users.repo.UserRepository;
import com.trash2cash.websocket.WebSocketNotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import com.trash2cash.users.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final FCMService fcmService;
    private final DeviceRepository deviceRepository;
    private final WebSocketNotificationService webSocketNotificationService;

    public Page<NotificationDto> getUserNotifications(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Page<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);

        return notifications.map(n -> NotificationDto.builder()
                .id(n.getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .readStatus(n.getReadStatus())
                .createdAt(n.getCreatedAt())
                .build()
        );
    }


    public NotificationResponse markAsRead(Long notificationId, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));

        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Unauthorized to mark this notification");
        }
        notification.setReadStatus(true);
        Notification updated = notificationRepository.save(notification);

        NotificationDto dto = NotificationDto.builder()
                .id(updated.getId())
                .title(updated.getTitle())
                .message(updated.getMessage())
                .readStatus(updated.getReadStatus())
                .createdAt(updated.getCreatedAt())
                .build();

        return new NotificationResponse("Notification marked as read successfully", dto);
    }


    public MarkAllReadResponse markAllAsRead(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndReadStatusFalse(user.getId());

        unreadNotifications.forEach(n -> n.setReadStatus(true));
        notificationRepository.saveAll(unreadNotifications);

        return new MarkAllReadResponse(unreadNotifications.size(), "All unread notifications marked as read successfully");
    }


    public Notification createNotification(String email, String title, String message) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
        Notification saved = notificationRepository.save(notification);
        NotificationDto dto = NotificationDto.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .message(saved.getMessage())
                .readStatus(saved.getReadStatus())
                .createdAt(saved.getCreatedAt())
                .build();

        webSocketNotificationService.sendNotification(email, dto);
        String token = deviceRepository.findFcmTokenByUserId(user.getId());
        if (token != null) {
            fcmService.sendPushNotification(token, "Trash2Cash", message);
        }
        return saved;

    }

    public CountResponse getUnreadCount(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Long unreadCount = notificationRepository.countByUserIdAndReadStatusFalse(user.getId());
        return CountResponse.builder()
                .unreadNotifications(unreadCount)
                .build();
    }

    public AnnouncementResponse broadcastAnnouncementToUsers(String senderEmail, String title, String message) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));

        List<User> users = userRepository.findByRoleIn(List.of(UserRole.GENERATOR, UserRole.RECYCLER));

        List<Notification> notifications = users.stream()
                .map(recipient  -> Notification.builder()
                        .title(title)
                        .message(message)
                        .user(recipient)
                        .sender(sender)
                        .createdAt(LocalDateTime.now())
                        .build())
                .toList();

        sendNotificationsToUsers(notifications, title, message);


        return AnnouncementResponse.builder()
                .message("Announcement broadcasted successfully!")
                .build();
    }

    public AnnouncementResponse broadcastAnnouncementToAdmins(String senderEmail, String title, String message) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));

        List<User> users = userRepository.findByRoleIn(List.of(UserRole.ADMIN));

        List<Notification> notifications = users.stream()
                .map(user -> Notification.builder()
                        .title(title)
                        .message(message)
                        .user(user)
                        .sender(sender)
                        .senderName(sender.getFirstName())
                        .createdAt(LocalDateTime.now())
                        .build())
                .toList();

        sendNotificationsToUsers(notifications, title, message);
        return AnnouncementResponse.builder()
                .message("Announcement broadcasted successfully!")
                .build();
    }



    public AnnouncementResponse sendCustomNotification(String senderEmail, List<String> recipientEmails, String title, String message) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));

        List<User> recipients = userRepository.findByEmailIn(recipientEmails);

        List<Notification> notifications = recipients.stream()
                .map(u -> Notification.builder()
                        .title(title)
                        .message(message)
                        .sender(sender)
                        .user(u)
                        .scope(NotificationScope.CUSTOM)
                        .createdAt(LocalDateTime.now())
                        .build())
                .toList();

        sendNotificationsToUsers(notifications, title, message);
        return AnnouncementResponse.builder()
                .message("Notification sent successfully!")
                .build();
    }




    private void sendNotificationsToUsers(List<Notification> notifications, String title, String message) {
        List<Notification> saved = notificationRepository.saveAll(notifications);

        List<NotificationDto> dtos = saved.stream()
                .map(n -> NotificationDto.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .message(n.getMessage())
                        .readStatus(n.getReadStatus())
                        .senderName("Trash2Cash")
                        .createdAt(n.getCreatedAt())
                        .build())
                .toList();

        List<Long> userIds = saved.stream()
                .map(n -> n.getUser().getId())
                .distinct()
                .toList();

        List<String> tokens = deviceRepository.findFcmTokensByUserIds(userIds);

        if (!tokens.isEmpty()) {
            fcmService.sendBatchPushNotification(tokens, "Trash2Cash", message);
        }

        saved.forEach(n -> {
            NotificationDto dto = dtos.stream()
                    .filter(d -> d.getId().equals(n.getId()))
                    .findFirst()
                    .orElse(null);
            if (dto != null) {
                webSocketNotificationService.sendNotification(n.getUser().getEmail(), dto);
            }
        });

        webSocketNotificationService.broadcastNotification(
                NotificationDto.builder()
                        .title(title)
                        .message(message)
                        .senderName("Trash2Cash")
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }


    public Page<NotificationDto> getNotificationsSentBy(String senderEmail, Pageable pageable) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Page<Notification> notifications = notificationRepository.findBySenderIdOrderByCreatedAtDesc(sender.getId(), pageable);

        return notifications.map(n -> NotificationDto.builder()
                .id(n.getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .readStatus(n.getReadStatus())
                .createdAt(n.getCreatedAt())
                .build());
    }


}

