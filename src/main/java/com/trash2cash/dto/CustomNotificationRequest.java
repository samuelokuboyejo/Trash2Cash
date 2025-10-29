package com.trash2cash.dto;

import lombok.Data;

import java.util.List;
@Data
public class CustomNotificationRequest {
    private List<String> recipients;
    private String title;
    private String message;
}
