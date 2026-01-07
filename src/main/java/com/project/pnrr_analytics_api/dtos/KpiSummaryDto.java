package com.project.pnrr_analytics_api.dtos;

import java.math.BigDecimal;

public record KpiSummaryDto(
        BigDecimal totalContractatEur,
        BigDecimal totalAbsorbitEur,
        Double rataAbsorbtieProcent,
        Long numarProiecte
) {
    // Putem adăuga un constructor compact pentru validări, dacă e cazul
}