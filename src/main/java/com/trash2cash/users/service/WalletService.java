package com.trash2cash.users.service;

import com.trash2cash.users.dto.WalletDto;
import com.trash2cash.users.dto.WithdrawRequest;
import com.trash2cash.users.enums.TransactionType;
import com.trash2cash.users.model.Transaction;
import com.trash2cash.users.model.User;
import com.trash2cash.users.model.Wallet;
import com.trash2cash.users.repo.TransactionRepository;
import com.trash2cash.users.repo.UserRepository;
import com.trash2cash.users.repo.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentGatewayService paymentGatewayService;
    private final UserRepository userRepository;

    public Wallet getUserWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user " + userId));
    }

    public Wallet createWalletForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.ZERO);

        return walletRepository.save(wallet);
    }

    @Transactional
    public WalletDto deposit(Long userId, BigDecimal amount) {
        Wallet wallet = getUserWallet(userId);
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        return  WalletDto.builder()
                .balance(wallet.getBalance())
                .id(wallet.getId())
                .points(wallet.getPoints())
                .build();
    }


    @Transactional
    public Transaction withdraw(WithdrawRequest request) {
        // 1. Fetch wallet
        Wallet wallet = walletRepository.findByUserId(1L) // TODO: replace with logged-in user
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }


        // 2. Call Paystack/Flutterwave Payout API
        boolean payoutSuccess = paymentGatewayService.processWithdrawal(
                request.getAmount(), request.getBankCode(), request.getAccountNumber()
        );

        if (!payoutSuccess) {
            throw new RuntimeException("Withdrawal failed");
        }

        // 3. Deduct balance
        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        walletRepository.save(wallet);

        // 4. Log transaction
        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .type(TransactionType.WITHDRAWAL)
                .status("SUCCESS")
                .user(wallet.getUser())
                .build();

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Wallet addPoints(Long userId, int points) {
        Wallet wallet = getUserWallet(userId);
        wallet.setPoints(wallet.getPoints() + points);
        return walletRepository.save(wallet);
    }
}
