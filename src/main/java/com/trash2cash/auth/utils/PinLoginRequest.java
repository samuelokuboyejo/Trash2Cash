package com.trash2cash.auth.utils;

import lombok.Data;

@Data
public class PinLoginRequest {
    private String pin;
    private String deviceId;
}
