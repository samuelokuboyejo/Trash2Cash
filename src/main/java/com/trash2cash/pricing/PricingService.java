package com.trash2cash.pricing;

import com.trash2cash.users.enums.WasteType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class PricingService {
    private static final Map<WasteType, BigDecimal> RATE_PER_KG = Map.of(
            WasteType.PLASTIC, new BigDecimal("500"),   // ₦500 per kg
            WasteType.GLASS, new BigDecimal("30"),     // ₦30 per kg
            WasteType.METAL, new BigDecimal("700")    // ₦700 per kg
    );

    public BigDecimal calculateAmount(WasteType type, double weightKg) {
        BigDecimal rate = RATE_PER_KG.getOrDefault(type, BigDecimal.ZERO);
        return rate.multiply(BigDecimal.valueOf(weightKg));
    }
}
