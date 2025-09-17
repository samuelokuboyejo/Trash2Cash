package com.trash2cash.scheduler;

import com.trash2cash.users.enums.WasteStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
@Data
@Builder
public class ScheduleDto {
    private Long scheduleId;
    private Long listingId;
    private String wasteTitle;
    private String wasteType;
    private double weight;
    private BigDecimal amount;
    private String recyclerName;
    private String recyclerRating;
    private LocalDate pickupDate;
    private LocalTime pickupTime;
    private String pickupLocation;
    private String imageUrl;
    private WasteStatus status;
}
