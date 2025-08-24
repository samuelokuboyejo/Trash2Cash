package com.trash2cash.users.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String firstName;
    private BigDecimal walletBalance;
    private Long points;

    private String  accessToken;

    private String  refreshToken;
}
