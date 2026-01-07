package com.project.pnrr_analytics_api.dtos;

import java.math.BigDecimal;

public record FundingRawDto(
        String fundingType,
        BigDecimal totalValue,
        Long projectCount
) {}