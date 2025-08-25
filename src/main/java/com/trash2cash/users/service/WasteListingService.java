package com.trash2cash.users.service;

import com.trash2cash.users.model.WasteListing;
import com.trash2cash.users.repo.WasteListingRepository;
import com.trash2cash.users.dto.WasteListingRequest;
import com.trash2cash.users.utils.ListingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WasteListingService {
    private final WasteListingRepository wasteListingRepository;

    public ListingResponse createListing(WasteListingRequest request) {
        var listing = WasteListing.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .pickupLocation(request.getPickupLocation())
                .type(request.getType())
                .unit(request.getUnit())
                .weight(request.getWeight())
                .contactPhone(request.getContactPhone())
                .imageUrl(request.getImageUrl()) // Save the URL here
                .build();

        WasteListing savedListing = wasteListingRepository.save(listing);
        return ListingResponse.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .pickupLocation(request.getPickupLocation())
                .type(request.getType())
                .unit(request.getUnit())
                .weight(request.getWeight())
                .contactPhone(request.getContactPhone())
                .imageUrl(request.getImageUrl())
                .build();

    }

    public List<WasteListing> getAllListings() {
        return wasteListingRepository.findAll();
    }
}
