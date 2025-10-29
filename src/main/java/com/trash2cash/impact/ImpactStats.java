package com.trash2cash.impact;

import com.trash2cash.users.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Table(name = "impact_stats")
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImpactStats {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private double co2Saved;

    private double energySaved;

    private double waterSaved;

    private double treesSaved;

    private int co2Percent;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;
}
