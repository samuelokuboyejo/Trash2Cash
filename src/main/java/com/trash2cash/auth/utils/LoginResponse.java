package com.trash2cash.auth.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private Long id;
    private String firstName;
    private BigDecimal walletBalance;
    private Long points;
    private Long unreadNotifications;

    private String  accessToken;

    private String  refreshToken;
}
