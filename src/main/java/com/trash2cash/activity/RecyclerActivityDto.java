package com.trash2cash.activity;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record RecyclerActivityDto(@Schema(description = "The waste title", example = "PET Plastic Bottles")
                                  String title,

                                  @Schema(description = "The weight of the waste in kg", example = "120.5")
                                  Double weight,

                                  @Schema(description = "Scheduled date and time for pickup", example = "2025-09-24T15:30:00")
                                  LocalDateTime scheduledDateTime,

                                  @Schema(description = "Pickup location", example = "Ikeja, Lagos")
                                  String pickupLocation,

                                  @Schema(description = "The current status of the waste listing", example = "SCHEDULED")
                                  String status) {
}
