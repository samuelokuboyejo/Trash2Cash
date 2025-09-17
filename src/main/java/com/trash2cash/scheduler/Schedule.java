package com.trash2cash.scheduler;

import com.trash2cash.users.model.User;
import com.trash2cash.waste.WasteListing;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "waste_listing_id", nullable = false)
    private WasteListing wasteListing;

    @ManyToOne
    @JoinColumn(name = "recycler_id", nullable = false)
    private User recycler;

    private LocalDate pickupDate;
    private LocalTime pickupTime;
    private String pickupLocation;

    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime rescheduledAt;

    private LocalDateTime updatedAt;
}
