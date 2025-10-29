package com.trash2cash.transfer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transfers")
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fromWalletId;

    private Long toWalletId;

    private BigDecimal amount;

    private UUID idempotencyKey;

    private LocalDateTime createdAt;

    private Long debitTransactionId;

    private Long creditTransactionId;
}
