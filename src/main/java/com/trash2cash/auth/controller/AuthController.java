package com.trash2cash.auth.controller;

import com.trash2cash.auth.service.AuthService;
import com.trash2cash.auth.service.JwtService;
import com.trash2cash.auth.service.RefreshTokenService;
import com.trash2cash.auth.utils.AuthResponse;
import com.trash2cash.auth.utils.LoginResponse;
import com.trash2cash.auth.utils.RoleRequest;
import com.trash2cash.auth.utils.UserResponse;
import com.trash2cash.users.dto.LoginRequest;
import com.trash2cash.users.dto.RegisterRequest;
import com.trash2cash.users.enums.UserRole;
import com.trash2cash.users.model.UserInfoUserDetails;
import com.trash2cash.users.service.UserService;
import com.trash2cash.users.utils.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
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

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns authentication tokens"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "409", description = "User already exists", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(
            summary = "Login user",
            description = "Authenticates user and returns JWT tokens"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }


    @Operation(
            summary = "Assign a role to a user",
            description = "Assigns a role (GENERATOR or RECYCLER) to a specific user. " +
                    "Also sets the user status to ACTIVE after assignment. " +
                    "⚠️ Requires a valid Bearer access token in the Authorization header.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role assigned successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid role value",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token",
                    content = @Content)
    })
    @PostMapping("/assign-role")
    public ResponseEntity<UserResponse> assignRole(
            @AuthenticationPrincipal UserInfoUserDetails principal,
            @RequestBody RoleRequest request) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = principal.getUsername();
        UserResponse updatedUser = userService.assignRole(email, request.getRole());
        return ResponseEntity.ok(updatedUser);
    }


//    @Operation(
//            summary = "Login with Google OAuth2",
//            description = "Authenticates a user via Google Sign-In. The client (Flutter app) must obtain a valid Google ID token from the Google Sign-In SDK and send it to this endpoint. " +
//                    "The backend verifies the token with Google's servers, creates a user account if it does not exist, and returns an authentication response containing access/refresh tokens."
//    )
//    @PostMapping("/google")
//    public ResponseEntity<AuthResponse> googleLogin(@RequestBody Map<String, String> body) throws Exception {
//        String idToken = body.get("idToken");
//        AuthResponse response = authService.loginWithGoogle(idToken);
//        return ResponseEntity.ok(response);
//    }
}
