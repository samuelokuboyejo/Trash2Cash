package com.trash2cash.waste;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface WasteListingRepository extends JpaRepository<WasteListing, Long> {

    @Query("SELECT COALESCE(SUM(w.weight), 0) FROM WasteListing w WHERE w.generator.id = :generatorId")
    Double sumWeightByGeneratorId(@Param("generatorId") Long generatorId);
    List<WasteListing> findByCreatedBy(Long userId);

    List<WasteListing> findByRecycler_Id(Long recyclerId);

    // (Optional) with pagination
    Page<WasteListing> findByRecycler_Id(Long recyclerId, Pageable pageable);
}
