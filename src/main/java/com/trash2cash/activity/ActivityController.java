package com.trash2cash.activity;
import com.trash2cash.users.model.UserInfoUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/activities")
@Tag(
        name = "User Activities",
        description = "Endpoints for retrieving user activity history such as scheduled pickups, payments, and completed transactions."
)
public class ActivityController {
    private final ActivityService activityService;

    @Operation(
            summary = "Fetch User Activity Timeline",
            description = """
                Returns a paginated timeline of a user's activities.
                Supports filtering by activity type:
                - **ALL** → fetch all activities
                - **PAID** → payments received for waste
                - **SCHEDULED** → upcoming or rescheduled pickups
                - **COMPLETED** → successfully completed pickups
                - ⚠️ Requires a valid Bearer access token in the Authorization header.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Activity retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    @GetMapping("/{type}")
    public ResponseEntity<Page<ActivityDto>> getActivity(
            @AuthenticationPrincipal UserInfoUserDetails principal,
            @PathVariable String type,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = principal.getUsername();
        Page<ActivityDto> activities = activityService.getUserActivity(email, type, pageable);
        return  ResponseEntity.ok(activities);
    }
}