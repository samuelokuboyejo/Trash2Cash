package com.trash2cash.transfer;

import com.trash2cash.notifications.NotificationService;
import com.trash2cash.transactions.Transaction;
import com.trash2cash.transactions.TransactionRepository;
import com.trash2cash.users.enums.TransactionStatus;
import com.trash2cash.users.enums.TransactionType;
import com.trash2cash.users.model.User;
import com.trash2cash.users.repo.UserRepository;
import com.trash2cash.users.service.UserService;
import com.trash2cash.wallet.Wallet;
import com.trash2cash.wallet.WalletRepository;
import com.trash2cash.wallet.WalletService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final TransactionRepository transactionRepository;
    private final TransferRepository transferRepository;
    private NotificationService notificationService;
    private UserRepository userRepository;
    private WalletRepository walletRepository;

    public Transfer transfer(String sourceUserEmail, TransferDto dto){
        User sourceUser = userRepository.findByEmail(sourceUserEmail).orElseThrow(() -> new EntityNotFoundException("Source user not found"));
        Wallet sourceWallet = walletRepository.findByUserId(sourceUser.getId()).orElseThrow(() -> new EntityNotFoundException("Source wallet not found"));
        User destinationUser = userRepository.findById(dto.getDestinationId()).orElseThrow(() -> new EntityNotFoundException("Destination user not found"));
        Wallet destinationWallet = walletRepository.findByUserId(destinationUser.getId()).orElseThrow(() -> new EntityNotFoundException("Destination wallet not found for this currency"));
        BigDecimal sourceBalance = sourceWallet.getBalance();

        if (sourceBalance.compareTo(dto.getAmount()) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }

        Transaction debitTransaction = createTransaction(
                dto.getAmount(),
                sourceWallet,
                TransactionType.DEBIT
        );

        Transaction creditTransaction = createTransaction(
                dto.getAmount(),
                destinationWallet,
                TransactionType.CREDIT
        );

        Transfer transfer = Transfer.builder()
                .amount(dto.getAmount())
                .fromWalletId(sourceWallet.getId())
                .toWalletId(destinationWallet.getId())
                .debitTransactionId(debitTransaction.getId())
                .creditTransactionId(creditTransaction.getId())
                .idempotencyKey(dto.getIdempotencyKey())
                .createdAt(LocalDateTime.now())
                .build();

        transferRepository.save(transfer);

        return transfer;
    }
    private Transaction createTransaction(BigDecimal amount, Wallet wallet, TransactionType type) {
        Transaction tx = Transaction.builder()
                .amount(amount)
                .walletId(wallet.getId())
                .type(type)
                .transactionStatus(TransactionStatus.SUCCESSFUL)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return transactionRepository.save(tx);
    }
}
