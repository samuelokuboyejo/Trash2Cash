package com.trash2cash.transactions;

import com.trash2cash.users.enums.TransactionType;
import com.trash2cash.users.enums.WithdrawalStatus;
import com.trash2cash.users.model.User;
import com.trash2cash.waste.WasteListing;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private WithdrawalStatus status;

    @OneToOne
    @JoinColumn(name = "waste_listing_id", nullable = false)
    private WasteListing wasteListing;

    private LocalDateTime createdAt = LocalDateTime.now();

}
