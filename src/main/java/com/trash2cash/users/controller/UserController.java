package com.trash2cash.users.controller;

import com.trash2cash.users.service.UserService;
import com.trash2cash.users.utils.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        UserProfileResponse response = userService.getProfile(email);
        return ResponseEntity.ok(response);
    }
}
