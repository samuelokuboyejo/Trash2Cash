package com.trash2cash.impact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImpactResponse {
    private double totalCo2;
    private double totalEnergy;
    private double totalWater;
    private double totalTrees;
    private double impactProgressPercentage;
    private double goalTarget;
}
