package com.trash2cash.scheduler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RescheduleResponse {
    private String message;
    private LocalDate newDate;
    private LocalTime newTime;
    private LocalDateTime rescheduledAt;
}
