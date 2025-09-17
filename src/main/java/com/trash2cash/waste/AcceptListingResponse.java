package com.trash2cash.waste;

import com.trash2cash.scheduler.ScheduleDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AcceptListingResponse {
    private String message;
    private Long listingId;
    private ScheduleDto schedule;
}
