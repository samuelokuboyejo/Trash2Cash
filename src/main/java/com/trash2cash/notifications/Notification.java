package com.trash2cash.notifications;

import com.trash2cash.users.enums.NotificationType;
import com.trash2cash.users.model.User;
import jakarta.persistence.*;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String message;
    private Boolean readStatus = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
