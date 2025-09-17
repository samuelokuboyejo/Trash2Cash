package com.trash2cash.transactions;

import com.trash2cash.users.enums.TransactionType;
import com.trash2cash.users.enums.WithdrawalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private WithdrawalStatus status;
    private LocalDateTime createdAt;
    private String message;
}
