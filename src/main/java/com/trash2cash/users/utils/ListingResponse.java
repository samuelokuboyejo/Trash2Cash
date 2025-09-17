package com.trash2cash.users.utils;

import com.trash2cash.users.enums.WasteStatus;
import com.trash2cash.users.enums.WasteType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingResponse {
    private Long id;
    private String title;
    private String description;
    private String pickupLocation;
    private WasteType type;
    private double unit;
    private double weight;
    private String contactPhone;
    private String imageUrl;
    private LocalDateTime time;
    private WasteStatus status;
    private Long createdBy;
}
