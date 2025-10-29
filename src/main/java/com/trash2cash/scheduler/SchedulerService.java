package com.trash2cash.scheduler;

import com.trash2cash.notifications.NotificationService;
import com.trash2cash.notifications.NotificationUtils;
import com.trash2cash.users.model.User;
import com.trash2cash.users.repo.UserRepository;
import com.trash2cash.users.utils.RecyclerUtils;
import com.trash2cash.waste.WasteListing;
import com.trash2cash.waste.WasteListingRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final ScheduleRepository scheduleRepository;
    private final WasteListingRepository wasteRepo;
    private final UserRepository userRepository;
    private final NotificationService notificationService;


    public ScheduleDto getScheduleDetails(Long listingId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        WasteListing listing = wasteRepo.findById(listingId)
                .orElseThrow(() -> new EntityNotFoundException("Listing not found"));

        if (!listing.getGenerator().getId().equals(user.getId()) &&
                (listing.getRecycler() == null || !listing.getRecycler().getId().equals(user.getId()))) {
            throw new AccessDeniedException("You are not authorized to view this schedule");
        }

        Schedule schedule = scheduleRepository.findByWasteListingId(listingId)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));

        return ScheduleDto.builder()
                .scheduleId(schedule.getId())
                .listingId(listing.getId())
                .wasteTitle(listing.getTitle())
                .wasteType(listing.getType().name())
                .weight(listing.getWeight())
                .amount(schedule.getWasteListing().getTransaction() != null
                        ? schedule.getWasteListing().getTransaction().getAmount() : BigDecimal.ZERO)
                .recyclerName(RecyclerUtils.getRecyclerDisplayName(schedule.getRecycler()))
                .pickupDate(schedule.getPickupDate())
                .pickupTime(schedule.getPickupTime())
                .pickupLocation(schedule.getPickupLocation())
                .imageUrl(listing.getImageUrl())
                .status(listing.getStatus())
                .build();
    }

    @Transactional
    public ConfirmScheduleResponse confirmSchedule(ConfirmDto dto, String email) {
        WasteListing listing = wasteRepo.findById(dto.getListingId())
                .orElseThrow(() -> new EntityNotFoundException("Listing not found"));

        Schedule schedule = scheduleRepository.findByWasteListingId(dto.getListingId())
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));

        // ensure only generator can confirm
        if (!listing.getRecycler().getEmail().equals(email)) {
            throw new AccessDeniedException("Unauthorized to confirm schedule");
        }

        schedule.setPickupLocation(dto.getPickupLocation());
        schedule.setPickupDate(dto.getPickupDate());
        schedule.setPickupTime(dto.getPickupTime());
        schedule.setAdditionalNotes(dto.getAdditionalNotes());
        schedule.setStatus(ScheduleStatus.CONFIRMED);
        schedule.setUpdatedAt(LocalDateTime.now());

        LocalDateTime scheduledDateTime = LocalDateTime.of(dto.getPickupDate(), dto.getPickupTime());

// Set in WasteListing
        listing.setScheduledDateTime(scheduledDateTime);
        scheduleRepository.save(schedule);

        notificationService.createNotification(
                listing.getGenerator().getEmail(),
                NotificationUtils.PICKUP_SCHEDULE,
                "Congratulations! You have successfully scheduled the pick"
        );

        notificationService.createNotification(
                listing.getRecycler().getEmail(),
                NotificationUtils.PICKUP_SCHEDULE,
                "Congratulations! You have successfully scheduled the pick"
        );

        return new ConfirmScheduleResponse("Schedule confirmed successfully", LocalDateTime.now());
    }

    public RescheduleResponse reschedulePickup(Long listingId, String email, RescheduleRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Schedule schedule = scheduleRepository.findByWasteListingId(listingId)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));

        schedule.setPickupDate(request.getNewDate());
        schedule.setPickupTime(request.getNewTime());
        schedule.setStatus(ScheduleStatus.RESCHEDULED);
        schedule.setRescheduledAt(LocalDateTime.now());

        scheduleRepository.save(schedule);
        String message = String.format(
                "Your pickup has been rescheduled to %s at %s. Reason: %s",
                request.getNewDate(),
                request.getNewTime(),
                request.getReason() != null ? request.getReason() : "No reason provided"
        );

        notificationService.createNotification(
                user.getEmail(),
                "Pickup Rescheduled",
                message
                                         // notify this user
        );


        return RescheduleResponse.builder()
                .message("Schedule rescheduled successfully")
                .newDate(request.getNewDate())
                .newTime(request.getNewTime())
                .rescheduledAt(schedule.getRescheduledAt())
                .build();
    }


    public ScheduleDto createSchedule(Long listingId, String email, LocalDate pickupDate, LocalTime pickupTime, String pickupLocation) {
        User recycler = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        WasteListing listing = wasteRepo.findById(listingId)
                .orElseThrow(() -> new EntityNotFoundException("Listing not found"));

        if (listing.getRecycler() == null || !listing.getRecycler().getId().equals(recycler.getId())) {
            throw new AccessDeniedException("Only the assigned recycler can create a schedule for this listing");
        }

        Schedule schedule = new Schedule();
        schedule.setWasteListing(listing);
        schedule.setRecycler(recycler);
        schedule.setPickupDate(pickupDate);
        schedule.setPickupTime(pickupTime);
        schedule.setPickupLocation(pickupLocation);
        schedule.setStatus(ScheduleStatus.PENDING);
        schedule.setCreatedAt(LocalDateTime.now());

        Schedule saved = scheduleRepository.save(schedule);
        
        return ScheduleDto.builder()
                .scheduleId(saved.getId())
                .listingId(listing.getId())
                .wasteTitle(listing.getTitle())
                .wasteType(listing.getType().name())
                .weight(listing.getWeight())
                .amount(listing.getTransaction() != null ? listing.getTransaction().getAmount() : BigDecimal.ZERO)
                .recyclerName(saved.getRecycler().getFirstName())
                .pickupDate(saved.getPickupDate())
                .pickupTime(saved.getPickupTime())
                .pickupLocation(saved.getPickupLocation())
                .imageUrl(listing.getImageUrl())
                .status(listing.getStatus())
                .build();
    }

}
