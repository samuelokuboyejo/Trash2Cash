package com.trash2cash.activity;

import com.trash2cash.transactions.TransactionRepository;
import com.trash2cash.users.enums.WasteStatus;
import com.trash2cash.users.model.User;
import com.trash2cash.users.repo.UserRepository;
import com.trash2cash.waste.WasteListing;
import com.trash2cash.waste.WasteListingRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final WasteListingRepository wasteRepo;
    private final TransactionRepository transactionRepo;
    private final UserRepository userRepository;

    public Page<ActivityDto> getUserActivity(String email, String type, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<ActivityDto> activities = new ArrayList<>();
        String normalizedType = (type == null) ? "ALL" : type.toUpperCase();


        List<WasteListing> userListings = wasteRepo.findByCreatedBy(user.getId()); //Fetch all listings by this user

        Map<WasteStatus, List<WasteListing>> listingsByStatus = userListings.stream()
                .filter(w -> w != null && w.getStatus() != null)
                .collect(Collectors.groupingBy(WasteListing::getStatus));

        if (normalizedType.equals("ALL") || normalizedType.equals("SCHEDULED")) {
            listingsByStatus.getOrDefault(WasteStatus.SCHEDULED, List.of()).forEach(w -> {
                String recyclerName = getRecyclerName(w);
                String scheduledTime = (w.getScheduledDateTime() != null)
                        ? w.getScheduledDateTime().toString()
                        : "unspecified time";

                Map<String, Object> safeData = new HashMap<>();
                safeData.put("recycler", recyclerName);
                safeData.put("scheduledDateTime", (w.getScheduledDateTime() != null) ? w.getScheduledDateTime() : "unspecified");

                activities.add(buildActivityDto(
                        ActivityType.SCHEDULED,
                        w,
                        "Pickup Scheduled - ",
                        recyclerName + " scheduled a pickup for " + scheduledTime,
                        safeData,
                        w.getCreatedAt()
                ));
            });
        }

        if (normalizedType.equals("ALL") || normalizedType.equals("COMPLETED")) {
            listingsByStatus.getOrDefault(WasteStatus.COMPLETED, List.of()).forEach(w -> {
                String recyclerName = getRecyclerName(w);

                Map<String, Object> safeData = new HashMap<>();
                safeData.put("recycler", recyclerName);

                activities.add(buildActivityDto(
                        ActivityType.COMPLETED,
                        w,
                        "Pickup Completed - ",
                        recyclerName + " confirmed pickup",
                        safeData,
                        w.getCreatedAt()
                ));
            });
        }

        if (normalizedType.equals("ALL") || normalizedType.equals("PAID")) {
            transactionRepo.findByUserId(user.getId()).forEach(t -> {
                WasteListing w = t.getWasteListing();

                if (w != null) {
                    Map<String, Object> safeData = new HashMap<>();
                    safeData.put("transactionId", t.getId());
                    safeData.put("listingTitle", w.getTitle());
                    safeData.put("amount", t.getAmount());

                    activities.add(new ActivityDto(
                            ActivityType.PAID,
                            "Payment Received - " + w.getTitle(),
                            "A recycler has paid ₦" + t.getAmount() + " for your waste",
                            safeData,
                            t.getCreatedAt()
                    ));
                } else {
                    // Withdrawal (no linked listing)
                    Map<String, Object> safeData = new HashMap<>();
                    safeData.put("transactionId", t.getId());
                    safeData.put("amount", t.getAmount());

                    activities.add(new ActivityDto(
                            ActivityType.WITHDRAWAL,
                            "Withdrawal Successful",
                            "₦" + t.getAmount() + " was withdrawn to your bank account",
                            safeData,
                            t.getCreatedAt()
                    ));
                }
            });
        }


        // Sort by timestamp descending
        List<ActivityDto> sorted = activities.stream()
                .sorted(Comparator.comparing(ActivityDto::timestamp).reversed())
                .toList();

        // Pagination
        int start = Math.max(0, (int) pageable.getOffset());
        int end = Math.min(start + pageable.getPageSize(), sorted.size());
        List<ActivityDto> pageContent = (start >= sorted.size()) ? List.of() : sorted.subList(start, end);

        return new PageImpl<>(pageContent, pageable, sorted.size());
    }

    public Page<RecyclerActivityDto> getRecyclerActivity(String email, Pageable pageable) {
        User recycler = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Recycler not found"));

        Page<WasteListing> recyclerListings = wasteRepo.findByRecycler_Id(recycler.getId(), pageable);

        List<RecyclerActivityDto> activities = recyclerListings.stream()
                .map(w -> new RecyclerActivityDto(
                        w.getTitle(),
                        w.getWeight(),
                        w.getScheduledDateTime(),
                        w.getPickupLocation(),
                        (w.getStatus() != null) ? w.getStatus().name() : "UNKNOWN"
                ))
                .toList();

        return new PageImpl<>(activities, pageable, recyclerListings.getTotalElements());
    }
    private ActivityDto buildActivityDto(
            ActivityType type,
            WasteListing w,
            String titlePrefix,
            String description,
            Map<String, Object> extraData,
            LocalDateTime timestamp
    ) {
        String safeTitle = (w != null && w.getTitle() != null) ? w.getTitle() : "Untitled Listing";
        Map<String, Object> safeData = new HashMap<>(extraData);

        if (w != null) {
            safeData.putIfAbsent("listingId", w.getId());
            safeData.putIfAbsent("listingTitle", safeTitle);
            safeData.putIfAbsent("amount", w.getAmount());
        }

        return new ActivityDto(
                type,
                titlePrefix + safeTitle,
                description,
                safeData,
                timestamp != null ? timestamp : LocalDateTime.now()
        );
    }

    private static String getRecyclerName(WasteListing w) {
        if (w == null || w.getRecycler() == null) {
            return "A recycler";
        }

        var recycler = w.getRecycler();

        String businessName = recycler.getBusinessName();
        if (businessName != null && !businessName.isBlank()) {
            return businessName;
        }

        String firstName = recycler.getFirstName();
        if (firstName != null && !firstName.isBlank()) {
            return firstName;
        }

        return "A recycler";
    }
}
