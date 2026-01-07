package com.project.pnrr_analytics_api.dtos;

import java.math.BigDecimal;

public record CriPerformanceDto(
        String institutionCode,
        String institutionName,
        BigDecimal allocatedEur,
        BigDecimal absorbedEur,
        Double performanceRate,
        Long activeProjects,
        String status // Ex: ON_TRACK, AT_RISK
) {}