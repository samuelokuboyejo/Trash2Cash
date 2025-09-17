package com.trash2cash.pricing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    List<PointHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
}

