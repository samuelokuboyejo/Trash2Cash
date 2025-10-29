package com.trash2cash.impact;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ImpactStatsRepository extends JpaRepository<ImpactStats, UUID> {

    List<ImpactStats> findByUserId(Long userId);
}
