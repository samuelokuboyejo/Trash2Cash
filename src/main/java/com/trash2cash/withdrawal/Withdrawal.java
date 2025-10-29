package com.trash2cash.withdrawal;

import com.trash2cash.users.enums.WithdrawalStatus;
import com.trash2cash.users.model.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "withdrawals")
@Builder
public class Withdrawal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;
    private String bankAccount;
    private WithdrawalStatus status; // ENUM: PENDING, SUCCESS, FAILED

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime createdAt;

}
