package com.trash2cash.users.model;

import com.trash2cash.users.enums.WasteType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import lombok.*;
import jakarta.persistence.*;

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
}
