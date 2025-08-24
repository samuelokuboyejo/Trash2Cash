package com.trash2cash.users.utils;

public class UserCreatedEvent {
    private final Long userId;

    public UserCreatedEvent(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
