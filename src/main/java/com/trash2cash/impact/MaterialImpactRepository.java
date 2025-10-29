package com.trash2cash.impact;

import com.trash2cash.users.enums.WasteType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MaterialImpactRepository extends JpaRepository<MaterialImpact, UUID> {
    Optional<MaterialImpact> findByMaterialType(WasteType materialType);

}
