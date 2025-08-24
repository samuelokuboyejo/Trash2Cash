package com.trash2cash.users.repo;

import com.trash2cash.users.model.WasteListing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WasteListingRepository extends JpaRepository<WasteListing, Long> {
}
