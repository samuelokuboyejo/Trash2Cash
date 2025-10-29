package com.trash2cash.impact;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "material_impact")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialImpact {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true)
    private String materialType;
    private double co2PerKg;
    private double energyPerKg;
    private double waterPerKg;
    private double treesPerKg;
    private int co2Percent;
}
