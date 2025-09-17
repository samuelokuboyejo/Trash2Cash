package com.trash2cash.auth.utils;

import com.trash2cash.users.enums.UserRole;
import lombok.Data;

@Data
public class RoleRequest {
    private UserRole role;

}
