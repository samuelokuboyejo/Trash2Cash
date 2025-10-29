package com.trash2cash.notifications;

import com.trash2cash.users.enums.NotificationScope;
import com.trash2cash.users.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String message;

    @Builder.Default
    private Boolean readStatus = false;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private NotificationScope scope;

    private String senderName;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
}
