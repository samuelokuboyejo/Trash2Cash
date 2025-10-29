package com.trash2cash.websocket;

import com.trash2cash.notifications.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String email, NotificationDto notification) {
        messagingTemplate.convertAndSendToUser(
                email,
                "/queue/notifications",
                notification
        );
    }

    public void broadcastNotification(NotificationDto notification) {
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }
}
