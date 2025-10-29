package com.trash2cash.leaderboard;

import com.trash2cash.leaderboard.dto.LeaderboardResponse;
import com.trash2cash.users.model.User;
import com.trash2cash.users.repo.UserRepository;
import com.trash2cash.wallet.Wallet;
import com.trash2cash.wallet.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderboardService {
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public List<LeaderboardResponse> getTopUsers(int limit, String email) {
        List<Wallet> wallets = walletRepository.findAllOrderByPointsDesc();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<LeaderboardResponse> leaderboard = new ArrayList<>();

        for (int i = 0; i < wallets.size(); i++) {
            Wallet w = wallets.get(i);
            if (w.getPoints() == null || w.getPoints() <= 0L) {
                continue;
            }
            int rank = i + 1;

            String displayName = w.getUser().getId().equals(currentUser.getId())
                    ? "You"
                    : w.getUser().getFirstName();

            leaderboard.add(new LeaderboardResponse(
                    rank,
                    displayName,
                    w.getPoints() + "pts"
            ));
        }

        boolean userInList = leaderboard.stream()
                .anyMatch(l -> l.getFirstName().equals("You"));

        if (!userInList) {
            Wallet userWallet = walletRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Wallet not found"));

            leaderboard.add(new LeaderboardResponse(
                    leaderboard.size() + 1,
                    "You",
                    (userWallet.getPoints() != null ? userWallet.getPoints() : 0) + "pts"
            ));
        }

        return leaderboard.stream()
                .limit(limit)
                .toList();
    }

    public List<LeaderboardResponse> getAllUsers(String email, int page, int size) {
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Wallet> walletPage = walletRepository.findAllOrderByPointsDesc(pageable);

        int startRank = page * size + 1;

        List<LeaderboardResponse> leaderboard = new ArrayList<>();
        for (int i = 0; i < walletPage.getContent().size(); i++) {
            Wallet w = walletPage.getContent().get(i);

            if ((w.getPoints() == null || w.getPoints() == 0) &&
                    !w.getUser().getId().equals(currentUser.getId())) {
                continue;
            }

            int rank = startRank + i;
            String displayName = w.getUser().getId().equals(currentUser.getId())
                    ? "You"
                    : w.getUser().getFirstName();

            leaderboard.add(new LeaderboardResponse(rank, displayName, w.getPoints() + "pts"));
        }

        boolean userInPage = leaderboard.stream().anyMatch(l -> l.getFirstName().equals("You"));
        if (!userInPage) {
            Wallet userWallet = walletRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Wallet not found"));

            leaderboard.add(new LeaderboardResponse(
                    startRank + walletPage.getContent().size(),
                    "You",
                    userWallet.getPoints() + "pts"
            ));
        }

        return leaderboard;
    }
}


