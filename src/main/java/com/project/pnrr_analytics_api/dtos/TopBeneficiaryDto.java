package com.project.pnrr_analytics_api.dtos;

import java.math.BigDecimal;

public record TopBeneficiaryDto(
        String numeBeneficiar,
        String cui,
        String tipBeneficiar,
        BigDecimal valoareContractataEur,
        Long numarProiecte
) {
    // Putem adăuga metode de formatare dacă e necesar
}