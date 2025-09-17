package com.trash2cash.users.dto;

import com.trash2cash.users.enums.UserRole;

public record UserProfileDto(  Long id,
                               String email,
                               String firstName,
                               String phone,
                               String imageUrl,
                               String location,
                               UserRole role) {

}
