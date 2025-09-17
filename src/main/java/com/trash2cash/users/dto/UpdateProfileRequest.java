package com.trash2cash.users.dto;
import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateProfileRequest(
        @Schema(example = "John Doe") String firstName,
        @Schema(example = "+2348012345678") String phone,
        @Schema(example = "Lagos, Nigeria") String location
) {}
