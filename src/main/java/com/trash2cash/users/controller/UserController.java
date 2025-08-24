package com.trash2cash.users.controller;

import com.trash2cash.users.model.User;
import com.trash2cash.users.service.UserService;
import com.trash2cash.users.utils.CustomUserDetails;
import com.trash2cash.users.utils.UserProfileResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
@Tag(name = "User profile", description = "Endpoints for getting the details of a user for the dashboard")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();

        UserProfileResponse response = userService.getProfile(user.getEmail());
        return ResponseEntity.ok(response);
    }
}
