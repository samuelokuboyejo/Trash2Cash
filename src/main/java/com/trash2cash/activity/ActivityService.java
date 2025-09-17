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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

        List<WasteListing> userListings = wasteRepo.findByCreatedBy(user.getId());

        Map<WasteStatus, List<WasteListing>> listingsByStatus = userListings.stream()
                .filter(w -> w.getStatus() != null)
                .collect(Collectors.groupingBy(WasteListing::getStatus));

//
//        if (normalizedType.equals("ALL")) {
//            listingsByStatus.getOrDefault(WasteStatus.OPEN, List.of()).forEach(w -> {
//                activities.add(new ActivityDto(
//                        ActivityType.OPEN,
//                        "Listing Open - " + w.getTitle(),
//                        "You created a listing which is now open and available for recyclers",
//                        Map.of(
//                                "listingId", w.getId(),
//                                "listingTitle", w.getTitle()
//                        ),
//                        w.getCreatedAt()
//                ));
//            });
//        }

        if (normalizedType.equals("ALL") || normalizedType.equals("SCHEDULED")) {
            listingsByStatus.getOrDefault(WasteStatus.SCHEDULED, List.of()).forEach(w -> {
                String recyclerName = w.getRecycler() != null ? w.getRecycler().getFirstName() : "A recycler";
                activities.add(new ActivityDto(
                        ActivityType.SCHEDULED,
                        "Pickup Scheduled - " + w.getTitle(),
                        recyclerName + " scheduled a pickup for " + w.getScheduledDateTime(),
                        Map.of(
                                "listingId", w.getId(),
                                "listingTitle", w.getTitle(),
                                "scheduledDateTime", w.getScheduledDateTime(),
                                "recycler", recyclerName
                        ),
                        w.getCreatedAt()
                ));
            });
        }

        if (normalizedType.equals("ALL") || normalizedType.equals("COMPLETED")) {
            listingsByStatus.getOrDefault(WasteStatus.COMPLETED, List.of()).forEach(w -> {
                String recyclerName = w.getRecycler() != null ? w.getRecycler().getFirstName() : "A recycler";
                activities.add(new ActivityDto(
                        ActivityType.COMPLETED,
                        "Pickup Completed - " + w.getTitle(),
                        recyclerName + " confirmed pickup",
                        Map.of(
                                "listingId", w.getId(),
                                "listingTitle", w.getTitle(),
                                "recycler", recyclerName
                        ),
                        w.getCreatedAt()
                ));
            });
        }
        if (normalizedType.equals("ALL") || normalizedType.equals("PAID")) {
            transactionRepo.findByUserId(user.getId()).forEach(t -> {
                WasteListing w = t.getWasteListing();
                activities.add(new ActivityDto(
                        ActivityType.PAID,
                        "Payment Received - " + w.getTitle(),
                        "A recycler has paid ₦" + t.getAmount() + " for your waste",
                        Map.of(
                                "transactionId", t.getId(),
                                "listingTitle", w.getTitle(),
                                "amount", t.getAmount()
                        ),
                        t.getCreatedAt()
                ));
            });
        }

        // --- Sort by timestamp descending ---
        List<ActivityDto> sorted = activities.stream()
                .sorted(Comparator.comparing(ActivityDto::timestamp).reversed())
                .toList();

        // --- Pagination ---
        int start = Math.max(0, (int) pageable.getOffset());
        int end = Math.min(start + pageable.getPageSize(), sorted.size());
        List<ActivityDto> pageContent = (start >= sorted.size()) ? List.of() : sorted.subList(start, end);

        return new PageImpl<>(pageContent, pageable, sorted.size());
    }


}
