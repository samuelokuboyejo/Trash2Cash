package com.trash2cash.notifications;

import com.trash2cash.users.model.UserInfoUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(
        name = "Notifications",
        description = "Endpoints for retrieving notifications."
)
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(
            summary = "Get all user notifications",
            description = "Fetches a paginated list of notifications for the authenticated user. " +
                    "Each notification includes details such as title, message, read/unread status, and creation timestamp."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Notifications retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NotificationDto.class),
                            examples = @ExampleObject(value = """
                                {
                                  "content": [
                                    {
                                      "id": 1,
                                      "title": "Withdrawal Successful",
                                      "message": "Your withdrawal of ₦45000 was successful",
                                      "readStatus": false,
                                      "createdAt": "2025-09-14T10:15:30"
                                    },
                                    {
                                      "id": 2,
                                      "title": "New Pickup Scheduled",
                                      "message": "Michael scheduled a pickup for Sep 17 10:00 AM",
                                      "readStatus": true,
                                      "createdAt": "2025-09-13T09:20:00"
                                    }
                                  ],
                                  "pageable": {
                                    "pageNumber": 0,
                                    "pageSize": 10
                                  },
                                  "totalElements": 2,
                                  "totalPages": 1,
                                  "last": true
                                }
                                """)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not allowed to access this resource")
    })
    @GetMapping("/all")
    public Page<NotificationDto> getNotifications(
            @AuthenticationPrincipal UserInfoUserDetails principal,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        String email = principal.getUsername();
        return notificationService.getUserNotifications(email, pageable);
    }



    @Operation(
            summary = "Mark a notification as read",
            description = "Marks the specified notification as read for the authenticated user. " +
                    "Returns the updated notification details."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Notification marked as read successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NotificationResponse.class),
                            examples = @ExampleObject(value = """
                                {
                                  "id": 1,
                                  "title": "Withdrawal Successful",
                                  "message": "Your withdrawal of ₦45000 was successful",
                                  "readStatus": true,
                                  "createdAt": "2025-09-14T10:15:30"
                                }
                                """)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User cannot update this notification"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    @PostMapping("/mark-read/{notificationId}")
    public ResponseEntity<NotificationResponse> markAsRead(
            @AuthenticationPrincipal UserInfoUserDetails principal,
            @PathVariable Long notificationId
    ) {
        String email = principal.getUsername();
        NotificationResponse response = notificationService.markAsRead(notificationId, email);
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Mark all notifications as read",
            description = "Marks all unread notifications for the authenticated user as read. " +
                    "Returns the number of notifications updated and a success message."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Unread notifications marked as read successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MarkAllReadResponse.class),
                            examples = @ExampleObject(value = """
                                {
                                  "count": 5,
                                  "message": "All unread notifications marked as read successfully"
                                }
                                """)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/mark-all-read")
    public ResponseEntity<MarkAllReadResponse> markAllAsRead(
            @AuthenticationPrincipal UserInfoUserDetails principal
    ) {
        String email = principal.getUsername();
        MarkAllReadResponse response = notificationService.markAllAsRead(email);
        return ResponseEntity.ok(response);
    }

}

