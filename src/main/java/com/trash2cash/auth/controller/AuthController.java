package com.trash2cash.auth.controller;

import com.trash2cash.auth.service.AuthService;
import com.trash2cash.auth.service.JwtService;
import com.trash2cash.auth.service.RefreshTokenService;
import com.trash2cash.auth.utils.AuthResponse;
import com.trash2cash.users.dto.LoginRequest;
import com.trash2cash.users.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {
    private final AuthService authService;
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
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }


    @Operation(
            summary = "Login with Google OAuth2",
            description = "Authenticates a user via Google Sign-In. The client (Flutter app) must obtain a valid Google ID token from the Google Sign-In SDK and send it to this endpoint. " +
                    "The backend verifies the token with Google's servers, creates a user account if it does not exist, and returns an authentication response containing access/refresh tokens."
    )
    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody Map<String, String> body) throws Exception {
        String idToken = body.get("idToken");
        AuthResponse response = authService.loginWithGoogle(idToken);
        return ResponseEntity.ok(response);
    }
}
