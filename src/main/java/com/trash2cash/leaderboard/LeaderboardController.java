package com.trash2cash.leaderboard;

import com.trash2cash.leaderboard.dto.LeaderboardResponse;
import com.trash2cash.users.model.UserInfoUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/leaderboard")
@Tag(name = "Leaderboard", description = "Endpoints for retrieving leaderboard information")
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    @Operation(
            summary = "Get top users by points",
            description = "Returns a list of top users sorted by points with rank and name. The logged-in user's name is replaced with 'You' if present.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LeaderboardResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping
    public ResponseEntity<List<LeaderboardResponse>> getLeaderboard(
            @Parameter(description = "Number of top users to return", example = "10")
            @RequestParam(defaultValue = "10") int limit,
            @AuthenticationPrincipal UserInfoUserDetails principal
    ) {
        return ResponseEntity.ok(
                leaderboardService.getTopUsers(limit, principal.getUsername())
        );
    }

    @Operation(
            summary = "Get all users with pagination",
            description = "Returns a paginated list of all users sorted by points with rank and name. The logged-in user's name is replaced with 'You' if present.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LeaderboardResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping("/all")
    public ResponseEntity<List<LeaderboardResponse>> getAllLeaderboard(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of users per page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserInfoUserDetails principal
    ) {
        return ResponseEntity.ok(
                leaderboardService.getAllUsers(principal.getUsername(), page, size)
        );
    };

}

