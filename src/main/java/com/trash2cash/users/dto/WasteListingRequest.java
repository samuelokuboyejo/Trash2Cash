package com.trash2cash.users.dto;
import lombok.*;
import com.trash2cash.users.enums.WasteType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WasteListingRequest {
    private String title;
    private String description;
    private String pickupLocation;
    private WasteType type;
    private double unit;
    private double weight;
    private String contactPhone;

}
