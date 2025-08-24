package com.trash2cash.users.service;

import com.trash2cash.users.model.User;
import com.trash2cash.users.model.Wallet;
import com.trash2cash.users.repo.UserRepository;
import com.trash2cash.users.repo.WalletRepository;
import com.trash2cash.users.utils.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        return UserProfileResponse.builder()
                .firstName(user.getFirstName())
                .id(user.getId())
                .walletBalance(wallet.getBalance())
                .points(wallet.getPoints())
                .build();
    }
}
