package com.trash2cash.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trash2cash.users.dto.UpdateProfileRequest;
import com.trash2cash.users.dto.UserProfileDto;
import com.trash2cash.users.model.UserInfoUserDetails;
import com.trash2cash.users.service.UserService;
import com.trash2cash.users.utils.CustomUserDetails;
import com.trash2cash.users.utils.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "User profile", description = "Endpoints for getting the details of a user for the dashboard")
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Get my profile",
            description = "Fetch the profile details of the currently authenticated user, including wallet balance and points. "
                    + "⚠️ Requires a valid Bearer access token in the Authorization header.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfileDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid access token",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User or wallet not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getMyProfile( @AuthenticationPrincipal UserInfoUserDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = principal.getUsername();
        return ResponseEntity.ok(userService.getProfile(email));
    }





    @Operation(
            summary = "Update user profile",
            description = "Allows an authenticated user to update their profile details and optionally upload a profile image. "
                    + "Only the owner of the profile can perform this action. "
                    + "⚠️ Requires a valid Bearer access token in the Authorization header.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfileDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized (missing or invalid access token)",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden (trying to update another user's profile)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)
    })
    @PutMapping(value = "/me/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileDto> updateProfile(
            @Parameter(
                    description = "Profile update request (JSON string in the multipart request). " +
                            "Send as part named 'data' with content-type application/json. " +
                            "Example: {\"firstName\":\"John Doe\",\"phone\":\"+2348012345678\",\"location\":\"Lagos, Nigeria\"}",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateProfileRequest.class,
                                    example = """
                                {
                                  "name": "John Doe",
                                  "phone": "+2348012345678",
                                  "location": "Lagos, Nigeria"
                                }
                                """)
                    )
            )
            @RequestPart("data") String reqJson,

            @Parameter(
                    description = "Optional profile image file (multipart file part)",
                    required = false
            )
            @RequestPart(value = "file", required = false) MultipartFile file,

            @AuthenticationPrincipal UserInfoUserDetails principal
    ) throws IOException {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = principal.getUsername();
        UpdateProfileRequest req = new ObjectMapper().readValue(reqJson, UpdateProfileRequest.class);
        return ResponseEntity.ok(userService.updateProfile(email, req, file));
    }





}
