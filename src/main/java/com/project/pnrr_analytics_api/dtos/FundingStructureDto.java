package com.project.pnrr_analytics_api.dtos;

import java.math.BigDecimal;

public record FundingStructureDto(
        String fundingType,
        BigDecimal totalValueEur,
        Double sharePercentage,
        Long projectCount
) {}