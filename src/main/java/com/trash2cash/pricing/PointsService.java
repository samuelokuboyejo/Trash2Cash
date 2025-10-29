package com.trash2cash.pricing;

import com.trash2cash.users.model.User;
import com.trash2cash.users.repo.UserRepository;
import com.trash2cash.wallet.Wallet;
import com.trash2cash.wallet.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointsService {

    private final UserRepository userRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final WalletRepository walletRepository;

    public int calculatePoints(double weightKg) {
        return (int) Math.round(weightKg * 5);    // Multiply weight × 5, then round to nearest integer
    }

    public void awardPoints(Long userId, int points, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        PointHistory history = PointHistory.builder()
                .user(user)
                .points(points)
                .reason(reason)
                .build();
        pointHistoryRepository.save(history);
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setPoints(wallet.getPoints() + points);
        walletRepository.save(wallet);
    }

    public void awardPointsForWaste(Long userId, double weightKg) {
        int points = calculatePoints(weightKg);
        awardPoints(userId, points, "Created a listing of " + weightKg + " kg");
    }
}
