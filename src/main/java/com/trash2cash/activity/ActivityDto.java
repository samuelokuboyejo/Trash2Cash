package com.trash2cash.activity;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(
        name = "Activity",
        description = "Represents a user activity such as payment received, pickup scheduled, or completed transaction."
)
public record ActivityDto(

        @Schema(
                description = "The type of activity",
                example = "PAID"
        )
        ActivityType type,

        @Schema(
                description = "Short title describing the activity",
                example = "Payment Received - PET Plastic Bottles"
        )
        String title,

        @Schema(
                description = "A brief description of the activity",
                example = "A recycler has paid ₦45,000 for your waste"
        )
        String description,

        @Schema(
                description = "Additional structured details about the activity",
                example = "{ \"transactionId\": 123, \"listingTitle\": \"PET Plastic Bottles\", \"amount\": 45000 }"
        )
        Map<String, Object> details,

        @Schema(
                description = "The timestamp when this activity occurred",
                example = "2025-09-14T10:15:30"
        )
        LocalDateTime timestamp
) {}
