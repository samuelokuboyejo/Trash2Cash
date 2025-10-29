package com.trash2cash.auth.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @Schema(description = "The refresh token issued during login", example = "d8f7c9a3-1a2b-4f5e-9c1d-b34f9c1e4a9a")
    private String refreshToken;

}
