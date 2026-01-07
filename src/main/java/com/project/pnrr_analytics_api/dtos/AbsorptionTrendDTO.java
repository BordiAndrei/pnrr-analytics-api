package com.project.pnrr_analytics_api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
public record AbsorptionTrendDTO(
        @JsonProperty("year")
        Integer year,

        @JsonProperty("month_index")
        Integer monthIndex,

        @JsonProperty("month_name")
        String monthName,

        @JsonProperty("monthly_absorption_eur")
        BigDecimal monthlyAbsorptionEur,

        @JsonProperty("cumulative_absorption_eur")
        BigDecimal cumulativeAbsorptionEur
) {
}