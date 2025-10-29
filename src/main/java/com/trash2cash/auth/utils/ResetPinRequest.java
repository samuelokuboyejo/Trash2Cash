package com.trash2cash.auth.utils;

import lombok.Data;

@Data
public class ResetPinRequest {
    private String oldPin;
    private String newPin;
}
