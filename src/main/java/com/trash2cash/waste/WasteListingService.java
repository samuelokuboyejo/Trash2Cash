package com.trash2cash.waste;

import com.trash2cash.notifications.NotificationService;
import com.trash2cash.notifications.NotificationUtils;
import com.trash2cash.pricing.PointsService;
import com.trash2cash.pricing.PricingService;
import com.trash2cash.scheduler.ScheduleDto;
import com.trash2cash.scheduler.SchedulerService;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WasteListingService {
    private final WasteListingRepository wasteListingRepository;
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final SchedulerService schedulerService;
    private final PricingService pricingService;
    private final PointsService pointsService;

    public ListingResponse createListing(WasteListingRequest request, MultipartFile file, String email) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        UploadResponse uploadResponse = cloudinaryService.upload(file);
        BigDecimal amount = pricingService.calculateAmount(request.getType(), request.getWeight());

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
                .generator(user)
                .amount(amount)
                .build();

        WasteListing savedListing = wasteListingRepository.save(listing);
        notificationService.createNotification(email, NotificationUtils.PENDING_LISTING, "Reminder: Your pending waste listing is awaiting pickup.");
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
                .amount(savedListing.getAmount())
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

    public AcceptListingResponse acceptListing(Long listingId, String recyclerEmail) {
        User recycler = userRepository.findByEmail(recyclerEmail)
                .orElseThrow(() -> new EntityNotFoundException("Recycler not found"));

        WasteListing listing = wasteListingRepository.findById(listingId)
                .orElseThrow(() -> new EntityNotFoundException("Listing not found"));

        if (listing.getRecycler() != null) {
            throw new IllegalStateException("Listing already accepted");
        }

        // Assign recycler to listing
        listing.setRecycler(recycler);
        listing.setStatus(WasteStatus.SCHEDULED);
        wasteListingRepository.save(listing);

        // Create default schedule
        ScheduleDto schedule = schedulerService.createSchedule(
                listingId,
                recyclerEmail,
                LocalDate.now().plusDays(1),  // default tomorrow
                LocalTime.of(10, 0),          // default 10 AM
                listing.getPickupLocation()
        );

        // Notify generator (listing owner)
        notificationService.createNotification(
                listing.getGenerator().getEmail(),
                "Listing Accepted",
                "Your listing has been accepted by " + recycler.getFirstName() +
                        ". Pickup scheduled for " + schedule.getPickupDate() +
                        " at " + schedule.getPickupTime()
        );

        pointsService.awardPoints(recycler.getId(), 10, "Accepted a listing");        return new AcceptListingResponse(
                "Listing accepted successfully. Schedule created.",
                listing.getId(),
                schedule
        );
    }

}
