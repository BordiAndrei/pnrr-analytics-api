package com.project.pnrr_analytics_api.dtos;

import java.math.BigDecimal;

// Folosit doar pentru a transfera datele brute din Query către Service
public record ComponentRawStatsDto(
        String code,
        String name,
        BigDecimal totalValue,
        BigDecimal absorbedValue,
        Long count
) {}