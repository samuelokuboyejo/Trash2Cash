package com.trash2cash.users.model;

import com.trash2cash.users.enums.TransactionType;
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

    private String status;

    @ManyToOne
    private User user;

    private LocalDateTime createdAt = LocalDateTime.now();
}
