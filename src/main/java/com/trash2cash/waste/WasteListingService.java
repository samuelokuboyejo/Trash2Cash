package com.trash2cash.waste;

import com.trash2cash.users.dto.WasteListingRequest;
import com.trash2cash.users.enums.WasteStatus;
import com.trash2cash.users.model.User;
import com.trash2cash.users.repo.UserRepository;
import com.trash2cash.users.service.CloudinaryService;
import com.trash2cash.users.utils.ListingResponse;
import com.trash2cash.users.utils.UploadResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WasteListingService {
    private final WasteListingRepository wasteListingRepository;
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;

    public ListingResponse createListing(WasteListingRequest request, MultipartFile file, String email) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        UploadResponse uploadResponse = cloudinaryService.upload(file);
        var listing = WasteListing.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .pickupLocation(request.getPickupLocation())
                .type(request.getType())
                .unit(request.getUnit())
                .weight(request.getWeight())
                .contactPhone(request.getContactPhone())
                .imageUrl(uploadResponse.getSecureUrl())
                .createdBy(user.getId())
                .status(WasteStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build();

        WasteListing savedListing = wasteListingRepository.save(listing);
        return ListingResponse.builder()
                .id(savedListing.getId())
                .title(savedListing.getTitle())
                .description(savedListing.getDescription())
                .pickupLocation(savedListing.getPickupLocation())
                .type(savedListing.getType())
                .unit(savedListing.getUnit())
                .weight(savedListing.getWeight())
                .contactPhone(savedListing.getContactPhone())
                .imageUrl(savedListing.getImageUrl())
                .time(savedListing.getCreatedAt())
                .status(savedListing.getStatus())
                .createdBy(savedListing.getCreatedBy())
                .build();
    }

    public List<ListingResponse> getAllListings() {
        List<WasteListing> listings = wasteListingRepository.findAll();
        return listings.stream()
                .map(listing -> ListingResponse.builder()
                        .id(listing.getId())
                        .title(listing.getTitle())
                        .description(listing.getDescription())
                        .pickupLocation(listing.getPickupLocation())
                        .type(listing.getType())
                        .unit(listing.getUnit())
                        .weight(listing.getWeight())
                        .contactPhone(listing.getContactPhone())
                        .imageUrl(listing.getImageUrl())
                        .time(listing.getCreatedAt())
                        .status(listing.getStatus())
                        .createdBy(listing.getCreatedBy())
                        .build())

                .toList();    }

    public List<ListingResponse> getListingsByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        List<WasteListing> listings = wasteListingRepository.findByCreatedBy(user.getId());

        return listings.stream()
                .map(listing -> ListingResponse.builder()
                        .id(listing.getId())
                        .title(listing.getTitle())
                        .description(listing.getDescription())
                        .pickupLocation(listing.getPickupLocation())
                        .type(listing.getType())
                        .unit(listing.getUnit())
                        .weight(listing.getWeight())
                        .contactPhone(listing.getContactPhone())
                        .imageUrl(listing.getImageUrl())
                        .time(listing.getCreatedAt())
                        .status(listing.getStatus())
                        .createdBy(listing.getCreatedBy())
                        .build())
                .toList();
    }
}
