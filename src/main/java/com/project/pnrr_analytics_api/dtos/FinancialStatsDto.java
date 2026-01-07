package com.project.pnrr_analytics_api.dtos;

import java.math.BigDecimal;

// Acest record "prinde" exact ce returnează SQL-ul (3 valori)
public record FinancialStatsDto(
        BigDecimal totalContractat,
        BigDecimal totalAbsorbit,
        Long count
) {}