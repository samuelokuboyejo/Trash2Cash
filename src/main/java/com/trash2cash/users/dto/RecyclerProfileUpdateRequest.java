package com.trash2cash.users.dto;

public record RecyclerProfileUpdateRequest(
        String firstName,
        String phone,
        String businessName,
        String businessType,
        String coverageArea
) {}
