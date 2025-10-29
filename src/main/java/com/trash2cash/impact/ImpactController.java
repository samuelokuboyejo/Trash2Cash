package com.trash2cash.impact;

import com.trash2cash.users.model.UserInfoUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/impact")
@Tag(name = "Impacts", description = "Endpoints for tracking and retrieving user environmental impact stats")
public class ImpactController {
    private final ImpactStatsService impactStatsService;

    @Operation(
            summary = "Get logged-in user’s environmental impact summary",
            description = "Retrieves the current authenticated user’s total CO₂ saved, energy saved, water saved, and other sustainability metrics.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(
                            name = "principal",
                            hidden = true,
                            description = "Injected authentication principal containing the logged-in user details"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved impact summary",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ImpactResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "User not authenticated",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Unexpected server error",
                            content = @Content
                    )
            }
    )
    @GetMapping("/me")
    public ResponseEntity<ImpactResponse> getUserImpact(@AuthenticationPrincipal UserInfoUserDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = principal.getUsername();
        ImpactResponse response = impactStatsService.getUserImpactSummary(email);
        return ResponseEntity.ok(response);
    }
}
