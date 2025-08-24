package com.trash2cash.users.utils;

import com.trash2cash.users.service.WalletService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class WalletEventListener {
    private final WalletService walletService;

    public WalletEventListener(WalletService walletService) {
        this.walletService = walletService;
    }

    @EventListener
    @Async
    public void handleUserCreated(UserCreatedEvent event) {
        walletService.createWalletForUser(event.getUserId());
    }
}
