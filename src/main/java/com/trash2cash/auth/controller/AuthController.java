package com.trash2cash.auth.controller;

import com.trash2cash.auth.service.AuthService;
import com.trash2cash.auth.service.JwtService;
import com.trash2cash.auth.service.RefreshTokenService;
import com.trash2cash.auth.utils.*;
import com.trash2cash.users.dto.LoginRequest;
import com.trash2cash.users.dto.RegisterRequest;
import com.trash2cash.users.model.UserInfoUserDetails;
import com.trash2cash.users.service.UserService;
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
import org.springframework.security.access.prepost.PreAuthorize;
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
            summary = "Login Admin",
            description = "Authenticates admin and returns JWT tokens"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    })
    @PostMapping("/login-admin")
    public ResponseEntity<LoginResponse> loginAdmin(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.loginAdmin(loginRequest));
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


    @Operation(
            summary = "Sign-up or Login with Google OAuth2",
            description = "Authenticates a user via Google Sign-In. The client (Flutter app) must obtain a valid Google ID token from the Google Sign-In SDK and send it to this endpoint. " +
                    "The backend verifies the token with Google's servers, creates a user account if it does not exist, and returns an authentication response containing access/refresh tokens."
    )
    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody Map<String, String> body) throws Exception {
        String idToken = body.get("idToken");
        AuthResponse response = authService.loginWithGoogle(idToken);
        return ResponseEntity.ok(response);
    }




    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh access token",
            description = "Takes a valid refresh token and issues a new access token and refresh token pair."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully refreshed access token",
            content = @Content(
                    schema = @Schema(implementation = RefreshTokenResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid or expired refresh token",
            content = @Content
    )
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The refresh token request containing the refresh token string",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RefreshTokenRequest.class)
                    )
            )
            @RequestBody RefreshTokenRequest request
    ) {
        RefreshTokenResponse tokenPair = refreshTokenService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(tokenPair);
    }


    @PostMapping("/set-pin")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Set up a 4-digit PIN for quick login",
            description = "Allows an authenticated user to create a secure 4-digit PIN for future logins on trusted devices.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PIN set successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid PIN format (must be 4 digits)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated")
    })
    public ResponseEntity<PinResponse> setPin(@AuthenticationPrincipal UserInfoUserDetails principal,
                                              @RequestBody PinRequest request) {
        PinResponse response = authService.setPin(principal.getUsername(), request.getPin());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-pin")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Reset your PIN securely",
            description = "Allows users to reset their 4-digit PIN after verifying their account password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PIN reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password or PIN format"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated")
    })
    public ResponseEntity<PinResponse> resetPin(@AuthenticationPrincipal UserInfoUserDetails principal,
                                                @RequestBody ResetPinRequest request) {
        PinResponse response = authService.resetPin(principal.getUsername(), request.getOldPin(), request.getNewPin());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register-device")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Register a trusted device",
            description = """
        Links the authenticated user account to a unique device ID, enabling PIN-based login on that specific device. 
        The `deviceId` should be generated and provided by the mobile frontend (Engr Sam Lucky) (e.g, using the device's unique identifier or a UUID stored locally).
        """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device registered successfully"),
            @ApiResponse(responseCode = "400", description = "Device already registered"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated")
    })
    public ResponseEntity<PinResponse> registerDevice(@AuthenticationPrincipal UserInfoUserDetails principal,
                                                      @RequestBody DeviceRequest request) {
        PinResponse response = authService.registerDevice(principal.getUsername(), request.getDeviceId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/pin-login")
    @Operation(summary = "Login using PIN and device ID",
            description = "Allows users to login using only their registered device ID and 4-digit PIN. Does not require email or password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Invalid device or PIN"),
            @ApiResponse(responseCode = "423", description = "Account temporarily locked due to too many failed attempts")
    })
    public ResponseEntity<LoginResponse> pinLogin(@RequestBody PinLoginRequest request) {
        return ResponseEntity.ok(authService.loginWithPin(request));
    }

    @DeleteMapping("/deregister-device")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Deregister a trusted device",
            description = "Removes a previously registered device from the user’s trusted list. The device will no longer support PIN login.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device deregistered successfully"),
            @ApiResponse(responseCode = "404", description = "Device not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated")
    })
    public ResponseEntity<PinResponse> deregisterDevice(@AuthenticationPrincipal UserInfoUserDetails principal,
                                                        @RequestBody DeviceRequest request) {
        PinResponse response = authService.deleteDevice(principal.getUsername(), request.getDeviceId());
        return ResponseEntity.ok(response);
    }
}
