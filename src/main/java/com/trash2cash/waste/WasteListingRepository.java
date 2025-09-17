package com.trash2cash.waste;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WasteListingRepository extends JpaRepository<WasteListing, Long> {
    List<WasteListing> findByGeneratorId(Long generatorId);

    List<WasteListing> findByCreatedBy(Long userId);

}
