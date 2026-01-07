package com.project.pnrr_analytics_api.dtos;

import java.math.BigDecimal;

// DTO simplu pentru a extrage doar ce ne trebuie din DB
public record LocatieValoareDto(
        String localitateNume,
        BigDecimal valoareEur
) {}