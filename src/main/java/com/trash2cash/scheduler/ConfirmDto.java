package com.trash2cash.scheduler;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
@Data
@Builder
public class ConfirmDto {
    private Long listingId;
    private String pickupLocation;
    private LocalDate pickupDate;
    private LocalTime pickupTime;
    private String additionalNotes;
}
