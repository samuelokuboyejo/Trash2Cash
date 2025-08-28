package com.trash2cash.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.auth.oauth2.JwtProvider;
import com.trash2cash.auth.utils.AuthResponse;
import com.trash2cash.auth.utils.GoogleTokenVerifier;
import com.trash2cash.users.dto.LoginRequest;
import com.trash2cash.users.dto.RegisterRequest;
import com.trash2cash.users.enums.Status;
import com.trash2cash.users.model.User;
import com.trash2cash.users.model.Wallet;
import com.trash2cash.users.repo.UserRepository;
import com.trash2cash.users.repo.WalletRepository;
import com.trash2cash.users.service.WalletService;
import com.trash2cash.users.utils.UserCreatedEvent;
import com.trash2cash.users.utils.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final WalletRepository walletRepository;
    private final ApplicationEventPublisher publisher;
    private final WalletService walletService;


        @Async
        public void createWalletAsync(Long userId) {
                walletService.createWalletForUser(userId);
            }

        public AuthResponse register(RegisterRequest request){
                if (userRepository.existsByEmail(request.getEmail())) {
                    throw new RuntimeException("Email already exists!");
                }
         if (userRepository.existsByFirstName(request.getFirstName())) {
            throw new RuntimeException("First name already exists!");
         }
        var user = User.builder()
                .firstName(request.getFirstName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(Status.ACTIVE)
                .termsAcceptedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(user);
        createWalletAsync(savedUser.getId());
        var accessToken = jwtService.generateToken(savedUser);
        var refreshToken = refreshTokenService.createRefreshToken(savedUser.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    public UserProfileResponse login(LoginRequest loginRequest){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword())
        );

        var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        var accessToken = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(loginRequest.getEmail());

        return UserProfileResponse.builder()
                .firstName(user.getFirstName())
                .id(user.getId())
                .walletBalance(wallet.getBalance())
                .points(wallet.getPoints())
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    public AuthResponse loginWithGoogle(String idToken) {
        GoogleIdToken.Payload payload = googleTokenVerifier.verify(idToken);

        String email = payload.getEmail();
        if (email == null || email.isBlank()) {
            throw new UsernameNotFoundException("Google account has no email");
        }

        // Build defaults from Google payload
        String displayName = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");

        // Find or create user
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User u = new User();
            u.setEmail(email);
            u.setStatus(Status.ACTIVE);
            u.setCreatedAt(LocalDateTime.now());
            u.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(u);
        });

        String accessToken = jwtService.generateToken(user.getId(), user.getEmail());
        var refresh = refreshTokenService.createRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refresh.getRefreshToken())
                .build();
    }
}
