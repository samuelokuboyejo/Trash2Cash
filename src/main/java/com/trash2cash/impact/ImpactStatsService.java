package com.trash2cash.impact;

import com.trash2cash.users.enums.WasteType;
import com.trash2cash.users.model.User;
import com.trash2cash.users.repo.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImpactStatsService {
    private final ImpactStatsRepository impactStatsRepository;
    private final MaterialImpactRepository materialImpactRepository;
    private final UserRepository userRepository;

    public ImpactStats recordImpact(User user, WasteType materialType, double weight) {
        MaterialImpact materialImpact = materialImpactRepository
                .findByMaterialType(materialType)
                .orElseThrow(() -> new IllegalArgumentException("Unknown material type: " + materialType));

        double co2Saved = weight * materialImpact.getCo2PerKg();
        double energySaved = weight * materialImpact.getEnergyPerKg();
        double waterSaved = weight * materialImpact.getWaterPerKg();
        double treesSaved = weight * materialImpact.getTreesPerKg();

        ImpactStats stats = ImpactStats.builder()
                .user(user)
                .co2Saved(co2Saved)
                .energySaved(energySaved)
                .waterSaved(waterSaved)
                .treesSaved(treesSaved)
                .co2Percent(materialImpact.getCo2Percent())
                .build();

        return impactStatsRepository.save(stats);
    }

    public ImpactResponse getUserImpactSummary(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("User not found"));
        List<ImpactStats> stats = impactStatsRepository.findByUserId(user.getId());

        if (stats.isEmpty()) {
            throw new EntityNotFoundException("No impact records found for user: " + email);
        }

        double totalCo2 = stats.stream().mapToDouble(ImpactStats::getCo2Saved).sum();
        double totalEnergy = stats.stream().mapToDouble(ImpactStats::getEnergySaved).sum();
        double totalWater = stats.stream().mapToDouble(ImpactStats::getWaterSaved).sum();
        double totalTrees = stats.stream().mapToDouble(ImpactStats::getTreesSaved).sum();

        double co2Goal = user.getCo2Goal() > 0 ? user.getCo2Goal() : 400;
        double progressPercentage = (totalCo2 / co2Goal) * 100;

        return ImpactResponse.builder()
                .totalCo2(totalCo2)
                .totalEnergy(totalEnergy)
                .totalWater(totalWater)
                .totalTrees(totalTrees)
                .impactProgressPercentage(progressPercentage)
                .goalTarget(co2Goal)
                .build();
    }

    private double calculateProgress(double totalCo2) {
        double dailyGoal = 400.0; // Example: daily CO₂ goal
        return (totalCo2 / dailyGoal) * 100;
    }
}
