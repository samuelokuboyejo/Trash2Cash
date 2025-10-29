package com.trash2cash.users.dto;

import com.trash2cash.users.enums.UserRole;

public record RecyclerProfileDto(Long id,
                                 String email,
                                 String firstName,
                                 String phone,
                                 String imageUrl,
                                 String businessName,
                                 String businessType,
                                 String coverageArea,
                                 UserRole role) {
}
