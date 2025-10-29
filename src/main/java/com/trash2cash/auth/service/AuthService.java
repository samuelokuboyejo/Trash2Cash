package com.trash2cash.auth.service;

import com.trash2cash.auth.utils.*;
import com.trash2cash.invitation.Invitation;
import com.trash2cash.invitation.InvitationRepository;
import com.trash2cash.notifications.NotificationRepository;
import com.trash2cash.users.dto.LoginRequest;
import com.trash2cash.users.dto.RegisterRequest;
import com.trash2cash.users.enums.Status;
import com.trash2cash.users.model.Device;
import com.trash2cash.users.model.User;
import com.trash2cash.users.repo.DeviceRepository;
import com.trash2cash.users.service.CloudinaryService;
import com.trash2cash.users.utils.UploadResponse;
import com.trash2cash.wallet.Wallet;
import com.trash2cash.users.repo.UserRepository;
import com.trash2cash.wallet.WalletRepository;
import com.trash2cash.wallet.WalletService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;
    private CloudinaryService cloudinaryService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final WalletRepository walletRepository;
    private final NotificationRepository notificationRepository;
    private final DeviceRepository deviceRepository;
    private final WalletService walletService;
    private final GoogleAuthService googleAuthService;



    @Async
        public void createWalletAsync(Long userId) {
                walletService.createWalletForUser(userId);
            }

        public AuthResponse register(RegisterRequest request){
                if (userRepository.existsByEmail(request.getEmail())) {
                    throw new RuntimeException("Email already exists!");
                }
         if (userRepository.existsByFirstName(request.getFirstName())) {
            throw new RuntimeException("Name already exists!");
         }
        var user = User.builder()
                .firstName(request.getFirstName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(Status.PENDING_ROLE)
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

    public LoginResponse login(LoginRequest loginRequest){
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
        Long unreadCount = notificationRepository.countByUserIdAndReadStatusFalse(user.getId());

        return LoginResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .points(wallet.getPoints())
                .walletBalance(wallet.getBalance())
                .unreadNotifications(unreadCount)
                .build();
    }


    public PinResponse setPin(String email, String pin) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (!pin.matches("\\d{4}")) {
            throw new IllegalArgumentException("PIN must be a 4-digit number");
        }

        user.setPinHash(passwordEncoder.encode(pin));
        userRepository.save(user);

        return  PinResponse.builder()
                .message("PIN set successfully")
                .build();
    }

    public LoginResponse loginWithPin(PinLoginRequest request) {
        Device device = deviceRepository.findByDeviceId(request.getDeviceId())
                .orElseThrow(() -> new EntityNotFoundException("Device not registered"));

        User user = userRepository.findByEmail(device.getUser().getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getPinHash() == null) {
            throw new RuntimeException("PIN not set. Please set up a PIN first.");
        }

        if (device.getLockoutEndTime() != null && device.getLockoutEndTime().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.LOCKED, "Too many failed attempts. Try again later.");
        }

        if (!passwordEncoder.matches(request.getPin(), user.getPinHash())) {
            int attempts = device.getFailedAttempts() + 1;
            device.setFailedAttempts(attempts);

            if (attempts >= 5) {
                device.setLockoutEndTime(LocalDateTime.now().plusMinutes(10));
                device.setFailedAttempts(0);
            }

            deviceRepository.save(device);
            throw new BadCredentialsException("Invalid PIN");
        }

        // Successful login
        device.setFailedAttempts(0);
        device.setLockoutEndTime(null);
        deviceRepository.save(device);

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        Long unreadCount = notificationRepository.countByUserIdAndReadStatusFalse(user.getId());
        var accessToken = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return LoginResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .points(wallet.getPoints())
                .walletBalance(wallet.getBalance())
                .unreadNotifications(unreadCount)
                .build();
    }

    public PinResponse registerDevice(String email, String deviceId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (deviceRepository.existsByDeviceId(deviceId)) {
            throw new RuntimeException("Device already registered");
        }

        Device device = new Device();
        device.setDeviceId(deviceId);
        device.setUser(user);
        device.setTrusted(true);

        deviceRepository.save(device);

        return  PinResponse.builder()
                .message("Device registered successfully")
                .build();
    }

    public PinResponse deleteDevice(String email, String deviceId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Device device = deviceRepository.findByDeviceIdAndUser(deviceId, user)
                .orElseThrow(() -> new EntityNotFoundException("Device not found"));

        deviceRepository.delete(device);
        return  PinResponse.builder()
                .message("Device unregistered successfully")
                .build();

    }

    public PinResponse resetPin(String email, String oldPin, String newPin) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPin, user.getPassword())) {
            throw new BadCredentialsException("Incorrect pin");
        }

        if (!newPin.matches("\\d{4}")) {
            throw new IllegalArgumentException("PIN must be a 4-digit number");
        }

        user.setPinHash(passwordEncoder.encode(newPin));
        userRepository.save(user);

        return  PinResponse.builder()
                .message("PIN reset successfully")
                .build();
    }

    public AuthResponse loginWithGoogle(String idToken) throws Exception {
        var payload = googleAuthService.verifyToken(idToken);

        String email = payload.getEmail();
        if (email == null || email.isBlank()) {
            throw new UsernameNotFoundException("Google account has no email");
        }
        String name = (String) payload.get("name");
        String picture = (String) payload.get("picture");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFirstName(name);
                    newUser.setImageUrl(picture);
                    newUser.setAuthProvider("google");
                    newUser.setCreatedAt(LocalDateTime.now());
                    newUser.setTermsAcceptedAt(LocalDateTime.now());
                    newUser.setStatus(Status.PENDING_ROLE);
                    User savedUser = userRepository.save(newUser);
                    createWalletAsync(savedUser.getId());
                    return savedUser;
                });
        String accessToken = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    public AuthResponse registerWithInvitation(String token, RegisterRequest request, MultipartFile file)throws IOException {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid invitation"));

        if (invitation.isUsed() || invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invitation expired or already used");
        }

        UploadResponse uploadResponse = cloudinaryService.upload(file);
        User user = User.builder()
                .email(invitation.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .role(invitation.getInvitedRole())
                .status(Status.ACTIVE)
                .imageUrl(uploadResponse.getSecureUrl())
                .createdAt(LocalDateTime.now())
                .build();

        user.setCreatedAt(LocalDateTime.now());
        invitation.setUsed(true);
        invitationRepository.save(invitation);

        User savedUser = userRepository.save(user);
        var accessToken = jwtService.generateToken(savedUser);
        var refreshToken = refreshTokenService.createRefreshToken(savedUser.getEmail());


        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();

    }


    public LoginResponse loginAdmin(LoginRequest loginRequest){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword())
        );

        var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(()-> new UsernameNotFoundException("User not found"));

        var accessToken = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(loginRequest.getEmail());
        Long unreadCount = notificationRepository.countByUserIdAndReadStatusFalse(user.getId());

        return LoginResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .unreadNotifications(unreadCount)
                .build();
    }

    @Transactional
    public DeleteResponse deleteUser(String email){
        var user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found"));

        notificationRepository.deleteByUser(user);
        notificationRepository.deleteBySender(user);
        userRepository.delete(user);
        return DeleteResponse.builder()
                .message("Account deleted successfully")
                .build();
    }


}
