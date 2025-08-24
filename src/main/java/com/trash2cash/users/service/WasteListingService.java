package com.trash2cash.users.service;

import com.trash2cash.users.model.WasteListing;
import com.trash2cash.users.repo.WasteListingRepository;
import com.trash2cash.users.dto.WasteListingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WasteListingService {
    private final WasteListingRepository wasteListingRepository;

    public WasteListing createListing(WasteListingRequest request, MultipartFile imageFile) {
        try {
            WasteListing listing = WasteListing.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .pickupLocation(request.getPickupLocation())
                    .type(request.getType())
                    .unit(request.getUnit())
                    .weight(request.getWeight())
                    .contactPhone(request.getContactPhone())
                    .image(imageFile.getBytes())
                    .build();

            return wasteListingRepository.save(listing);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save listing with image", e);
        }
    }

    public List<WasteListing> getAllListings() {
        return wasteListingRepository.findAll();
    }
}
