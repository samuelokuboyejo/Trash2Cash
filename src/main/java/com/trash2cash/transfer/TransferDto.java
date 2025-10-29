package com.trash2cash.transfer;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class TransferDto {
    private Long destinationId;

    private BigDecimal amount;

    private UUID idempotencyKey;

}
