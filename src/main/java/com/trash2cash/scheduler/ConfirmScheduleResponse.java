package com.trash2cash.scheduler;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
public class ConfirmScheduleResponse {
    private String message;
    private LocalDateTime confirmedAt;
}
