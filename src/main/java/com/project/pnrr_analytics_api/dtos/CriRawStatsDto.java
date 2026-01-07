package com.project.pnrr_analytics_api.dtos;

import java.math.BigDecimal;

// DTO intern pentru a scoate sumele din baza de date
public record CriRawStatsDto(
        String cod,
        String denumire,
        BigDecimal totalAlocat,
        BigDecimal totalAbsorbit,
        Long numarProiecte
) {}