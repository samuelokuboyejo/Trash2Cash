package com.trash2cash.users.service;

import com.trash2cash.auth.utils.AccountStatusResponse;
import com.trash2cash.auth.utils.UserResponse;
import com.trash2cash.exceptions.UserNotFoundException;
import com.trash2cash.exceptions.WalletNotFoundException;
import com.trash2cash.users.dto.*;
import com.trash2cash.users.enums.Status;
import com.trash2cash.users.enums.UserRole;
import com.trash2cash.users.model.User;
import com.trash2cash.users.repo.UserRepository;
import com.trash2cash.users.utils.UploadResponse;
import com.trash2cash.users.utils.UserProfileResponse;
import com.trash2cash.wallet.WalletDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;


    public UserProfileDto getProfile(String email) {
        User user = userRepository.findByEmailWithWallet(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        if (user.getWallet() == null) {
            throw new WalletNotFoundException("Wallet not found for user: " + user.getId());
        }

        return mapToUserProfileResponse(user);
    }

    private UserProfileDto mapToUserProfileResponse(User user) {
        return new UserProfileDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getPhone(),
                user.getImageUrl(),
                user.getLocation(),
                user.getRole()
        );
    }

    @Transactional
    public UserProfileDto updateProfile(String email, UpdateProfileRequest req, MultipartFile file) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setFirstName(req.firstName());
        user.setPhone(req.phone());
        user.setLocation(req.location());

        if (file != null && !file.isEmpty()) {
            UploadResponse uploadResponse = cloudinaryService.upload(file);
            user.setImageUrl(uploadResponse.getSecureUrl());
        }

        userRepository.save(user);

        return new UserProfileDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getPhone(),
                user.getImageUrl(),
                user.getLocation(),
                user.getRole()
        );
    }

    public UserResponse assignRole(String email, UserRole role) {
        log.info("Assigning role '{}' to user with email '{}'", role, email);

        User user;
        try {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        } catch (Exception e) {
            log.error("Error fetching user by email '{}': {}", email, e.getMessage(), e);
            throw e;
        }

        try {
            user.setRole(role);
            user.setStatus(Status.ACTIVE);
            user.setUpdatedAt(LocalDateTime.now());

            user = userRepository.save(user);

            log.info("Successfully assigned role '{}' to user '{}'", role, email);
        } catch (Exception e) {
            log.error("Error saving user with email '{}': {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to assign role due to server error");
        }

        return new UserResponse(user.getId(), user.getEmail(), user.getRole(), user.getStatus());
    }

    @Transactional
    public RecyclerProfileDto updateRecyclerProfile(String email, RecyclerProfileUpdateRequest req, MultipartFile file) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setFirstName(req.firstName());
        user.setPhone(req.phone());
        user.setLocation(req.coverageArea());
        user.setCoverageArea(req.coverageArea());
        user.setBusinessName(req.businessName());
        user.setBusinessType(req.businessType());

        if (file != null && !file.isEmpty()) {
            UploadResponse uploadResponse = cloudinaryService.upload(file);
            user.setImageUrl(uploadResponse.getSecureUrl());
        }

        userRepository.save(user);

        return new RecyclerProfileDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getPhone(),
                user.getImageUrl(),
                user.getBusinessName(),
                user.getBusinessType(),
                user.getCoverageArea(),
                user.getRole()
        );
    }

    public List<UserRoleProfileResponse> getAllPrivilegedUsers(){
        List<User> users = userRepository.findByRoleIn(List.of(UserRole.ADMIN));
        return users.stream()
                .map(user -> UserRoleProfileResponse.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .email(user.getEmail())
                        .imageUrl(user.getImageUrl())
                        .role(user.getRole())
                        .lastLogin(user.getLastLogin())
                        .dateJoined(user.getCreatedAt())
                        .build())

                .toList();
    }


    public AccountStatusResponse changeAccountStatus(String email, Status status) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setStatus(status);
        userRepository.save(user);
//        emailService.sendAccountStatusEmail(
//                user.getEmail(),
//                user.getFirstName() + " " + user.getLastName(),
//                status
//        );
        return AccountStatusResponse.builder()
                .message( "User account " + status.name().toLowerCase() + " successfully.")
                .build();
    }

}
