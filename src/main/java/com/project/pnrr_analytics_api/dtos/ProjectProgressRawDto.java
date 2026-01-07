package com.project.pnrr_analytics_api.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record ProjectProgressRawDto(
        UUID id,
        String titlu,
        BigDecimal progresTehnic,
        BigDecimal progresFinanciar,
        BigDecimal diferentaTehnicFinanciar,
        BigDecimal valoareEur,
        String numeBeneficiar
) {}