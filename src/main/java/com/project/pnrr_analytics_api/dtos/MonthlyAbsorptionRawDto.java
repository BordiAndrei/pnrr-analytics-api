package com.project.pnrr_analytics_api.dtos;

import java.math.BigDecimal;

// DTO simplu pentru rezultatul GROUP BY din SQL
public record MonthlyAbsorptionRawDto(
        Integer year,
        String monthName,
        BigDecimal totalAbsorbed
) {}