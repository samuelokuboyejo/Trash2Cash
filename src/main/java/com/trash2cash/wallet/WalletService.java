package com.trash2cash.wallet;

import com.trash2cash.notifications.NotificationService;
import com.trash2cash.notifications.NotificationUtils;
import com.trash2cash.transactions.TransactionDto;
import com.trash2cash.users.dto.WalletDto;
import com.trash2cash.users.dto.WithdrawRequest;
import com.trash2cash.users.enums.TransactionType;
import com.trash2cash.transactions.Transaction;
import com.trash2cash.users.enums.WithdrawalStatus;
import com.trash2cash.users.model.User;
import com.trash2cash.users.service.PaymentGatewayService;
import com.trash2cash.transactions.TransactionRepository;
import com.trash2cash.users.repo.UserRepository;
import com.trash2cash.withdrawal.Withdrawal;
import com.trash2cash.withdrawal.WithdrawalRepository;
import jakarta.persistence.EntityNotFoundException;
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
    private final NotificationService notificationService;
    private final WithdrawalRepository withdrawalRepository;


    public Wallet getUserWallet(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found for user " + user.getId()));
    }

    @Transactional
    public Wallet createWalletForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.ZERO);

        return walletRepository.save(wallet);
    }

    @Transactional
    public WalletDto deposit(String email, BigDecimal amount) {
        Wallet wallet = getUserWallet(email);
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
    public TransactionDto withdraw(WithdrawRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        // Call Paystack/Flutterwave Payout API
        boolean payoutSuccess = paymentGatewayService.processWithdrawal(
                request.getAmount(),
                request.getBankCode(),
                request.getAccountNumber()
        );

        if (!payoutSuccess) {
            throw new IllegalStateException("Withdrawal failed, please try again later");
        }

        // Deduct balance
        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .type(TransactionType.WITHDRAWAL)
                .status(WithdrawalStatus.SUCCESSFUL)
                .user(wallet.getUser())
                .createdAt(LocalDateTime.now())
                .build();
        Transaction saved = transactionRepository.save(transaction);

        Withdrawal withdrawal = Withdrawal.builder()
                .amount(transaction.getAmount())
                .bankAccount(request.getAccountNumber().concat(request.getBankCode()))
                .status(WithdrawalStatus.SUCCESSFUL)
                .createdAt(LocalDateTime.now())
                .user(wallet.getUser())
                .build();
        withdrawalRepository.save(withdrawal);

        notificationService.createNotification(
                email,
                NotificationUtils.WITHDRAWAL,
                String.format("Your withdrawal of ₦%,.2f was successful", request.getAmount())
        );

        return TransactionDto.builder()
                .id(saved.getId())
                .amount(saved.getAmount())
                .type(saved.getType())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
                .message("Withdrawal processed successfully")
                .build();
    }


    @Transactional
    public WalletDto addPoints(String email, int points) {
        Wallet wallet = getUserWallet(email);
        wallet.setPoints(wallet.getPoints() + points);
        return  WalletDto.builder()
                .balance(wallet.getBalance())
                .id(wallet.getId())
                .points(wallet.getPoints())
                .build();
    }
}
