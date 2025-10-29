package com.trash2cash.notifications;

import com.trash2cash.dto.AnnouncementResponse;
import com.trash2cash.dto.BroadcastRequest;
import com.trash2cash.dto.CustomNotificationRequest;
import com.trash2cash.users.model.UserInfoUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
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


    @Operation(
            summary = "Get unread notifications count (Requires Bearer Token)",
            description = """
            **This endpoint requires a valid Bearer token in the `Authorization` header.**
            
            Retrieves the total number of unread notifications for the currently authenticated user.
            The user is identified through the JWT access token provided at login.
            """,
            security = { @SecurityRequirement(name = "bearerAuth") },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Unread notification count retrieved successfully",
                            content = @Content(schema = @Schema(implementation = CountResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Bearer token is missing or invalid"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    @GetMapping("/unread-count")
    public ResponseEntity<CountResponse> getUnreadCount(@AuthenticationPrincipal UserInfoUserDetails principal){
        String email = principal.getUsername();
        CountResponse response = notificationService.getUnreadCount(email);
        return ResponseEntity.ok(response);
    }






    @Operation(
            summary = "Broadcast announcement to all users(recyclers and generators)",
            description = "Allows an ADMIN to send a system-wide announcement to all students."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Announcement broadcasted successfully",
                    content = @Content(schema = @Schema(implementation = AnnouncementResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized – user must be logged in"),
            @ApiResponse(responseCode = "403", description = "Forbidden – only ADMIN can access this endpoint")
    })
    @PostMapping("/broadcast/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnnouncementResponse> broadcastAnnouncementToUsers(
            @Parameter(hidden = true) @AuthenticationPrincipal UserInfoUserDetails principal,
            @RequestBody BroadcastRequest request) {

        AnnouncementResponse response = notificationService.broadcastAnnouncementToUsers(
                principal.getUsername(),
                request.getTitle(),
                request.getMessage()
        );
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Broadcast announcement to admins",
            description = "Allows an ADMIN to send a system-wide announcement to all admins."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Announcement sent to admins successfully",
                    content = @Content(schema = @Schema(implementation = AnnouncementResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized – user must be logged in"),
            @ApiResponse(responseCode = "403", description = "Forbidden – only ADMIN can access this endpoint")
    })
    @PostMapping("/broadcast/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnnouncementResponse> broadcastAnnouncementToAdminsAndStaff(
            @Parameter(hidden = true) @AuthenticationPrincipal UserInfoUserDetails principal,
            @RequestBody BroadcastRequest request) {

        AnnouncementResponse response = notificationService.broadcastAnnouncementToAdmins(
                principal.getUsername(),
                request.getTitle(),
                request.getMessage()
        );
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Send custom notification",
            description = "Allows ADMIN to send custom notifications to specific users (recyclers or generators)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Custom notification sent successfully",
                    content = @Content(schema = @Schema(implementation = AnnouncementResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized – user must be logged in"),
            @ApiResponse(responseCode = "403", description = "Forbidden – only ADMIN can access this endpoint")
    })
    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<AnnouncementResponse> sendCustom(
            @Parameter(hidden = true) @AuthenticationPrincipal UserInfoUserDetails principal,
            @RequestBody CustomNotificationRequest request) {

        AnnouncementResponse response = notificationService.sendCustomNotification(
                principal.getUsername(),
                request.getRecipients(),
                request.getTitle(),
                request.getMessage()
        );
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all sent notifications",
            description = "Allows an ADMIN to view all notifications they have previously sent."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sent notifications retrieved successfully",
                    content = @Content(schema = @Schema(implementation = NotificationDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized – user must be logged in"),
            @ApiResponse(responseCode = "403", description = "Forbidden – only ADMIN can access this endpoint")
    })
    @GetMapping("/sent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<NotificationDto>> getSentNotifications(
            @Parameter(hidden = true) @AuthenticationPrincipal UserInfoUserDetails principal,
            Pageable pageable) {
        return ResponseEntity.ok(notificationService.getNotificationsSentBy(principal.getUsername(), pageable));
    }
}

