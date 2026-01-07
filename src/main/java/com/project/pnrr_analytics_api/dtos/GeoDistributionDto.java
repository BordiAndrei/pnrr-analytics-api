package com.project.pnrr_analytics_api.dtos;

import java.math.BigDecimal;

public record GeoDistributionDto(
        String judet,
        String regiune,
        BigDecimal valoareTotalaEur,
        Long numarProiecte
) {
    // Putem adăuga metode ajutătoare aici dacă e nevoie,
    // de exemplu pentru a formata regiunea sau a calcula o medie simplă.
}