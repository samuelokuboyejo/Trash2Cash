package com.trash2cash.pricing;

import com.trash2cash.users.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private int points;
    private String reason;  // e.g. "Pickup Completed", "Referral Bonus"

    private LocalDateTime createdAt = LocalDateTime.now();
}

