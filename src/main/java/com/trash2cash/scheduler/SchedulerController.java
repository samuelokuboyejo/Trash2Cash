package com.trash2cash.scheduler;

import com.trash2cash.users.model.UserInfoUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scheduler")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Scheduler", description = "Endpoints for managing waste pickup schedules")
public class SchedulerController {
    private final SchedulerService schedulerService;

    @Operation(
            summary = "Get schedule details",
            description = "Fetches the schedule details for a specific waste listing, including recycler info, pickup date, time, and location."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schedule details retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Schedule or listing not found")
    })
    @GetMapping("/{listingId}")
    public ResponseEntity<ScheduleDto> getScheduleDetails(
            @AuthenticationPrincipal UserInfoUserDetails principal,
            @Parameter(description = "ID of the waste listing") @PathVariable Long listingId
    ) {
        String email = principal.getUsername();
        ScheduleDto schedule = schedulerService.getScheduleDetails(listingId, email);
        return ResponseEntity.ok(schedule);
    }


    @Operation(
            summary = "Confirm a schedule",
            description = "Allows the waste generator (listing owner) to confirm the recycler's scheduled pickup. " +
                    "⚠️ Requires Bearer Token authentication."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schedule confirmed successfully",
                    content = @Content(schema = @Schema(implementation = ConfirmScheduleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid Bearer Token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - only the waste generator can confirm"),
            @ApiResponse(responseCode = "404", description = "Schedule or listing not found")
    })
    @PostMapping("/confirm")
    public ResponseEntity<ConfirmScheduleResponse> confirmSchedule(
            @AuthenticationPrincipal UserInfoUserDetails principal,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Schedule confirmation details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ConfirmDto.class))
            )
            @RequestBody ConfirmDto dto
    ) {
        String email = principal.getUsername();
        ConfirmScheduleResponse response = schedulerService.confirmSchedule(dto, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(
            summary = "Reschedule a pickup",
            description = "Recycler or listing owner can propose a new date and time for the pickup."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pickup rescheduled successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Schedule or listing not found")
    })
    @PatchMapping("/{listingId}/reschedule")
    public ResponseEntity<RescheduleResponse> reschedulePickup(
            @AuthenticationPrincipal UserInfoUserDetails principal,
            @PathVariable Long listingId,
            @RequestBody RescheduleRequest request
    ) {
        String email = principal.getUsername();
        RescheduleResponse response = schedulerService.reschedulePickup(listingId, email, request);
        return ResponseEntity.ok(response);
    }

}
