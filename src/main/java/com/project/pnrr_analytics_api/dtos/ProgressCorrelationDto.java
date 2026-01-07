package com.project.pnrr_analytics_api.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record ProgressCorrelationDto(
        UUID projectId,
        String title,
        BigDecimal techProgressPercent,
        BigDecimal finProgressPercent,
        BigDecimal gapPercent,     // Diferența (Tehnic - Financiar)
        BigDecimal valueEur,
        String beneficiary,
        String riskFlag            // Calculat în Service (HIGH, MODERATE, LOW)
) {}