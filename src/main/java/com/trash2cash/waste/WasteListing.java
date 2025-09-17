package com.trash2cash.waste;

import com.trash2cash.users.enums.WasteStatus;
import com.trash2cash.users.enums.WasteType;
import com.trash2cash.transactions.Transaction;
import com.trash2cash.users.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WasteListing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String pickupLocation;

    @Enumerated(EnumType.STRING)
    private WasteType type;

    private double unit;
    private double weight;

    private String contactPhone;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private WasteStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User generator;

    @OneToOne(mappedBy = "wasteListing")
    private Transaction transaction;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "recycler_id")
    private User recycler;

    private Long createdBy;

    private LocalDateTime scheduledDateTime;

}
