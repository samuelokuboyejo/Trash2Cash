package com.trash2cash.auth.utils;

import com.trash2cash.users.enums.Status;
import com.trash2cash.users.enums.UserRole;

public record UserResponse(Long id, String email, UserRole role, Status status) {
}
