package com.trash2cash.auth.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshTokenResponse {
    @Schema(description = "Newly issued access token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;

    @Schema(description = "Newly issued refresh token", example = "a1b2c3d4-5678-90ab-cdef-1234567890ab")
    private String refreshToken;
}
