package com.trash2cash.reviews;

import com.trash2cash.users.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating; // 1 - 5
    private String feedback;

    @ManyToOne
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewerId;

    @ManyToOne
    @JoinColumn(name = "target_user_id", nullable = false)
    private User targetUserId;

    private LocalDateTime createdAt;
}