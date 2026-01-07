package com.project.pnrr_analytics_api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record ComponentBreakdownDTO(
        @JsonProperty("component_code")
        String componentCode,

        @JsonProperty("component_name")
        String componentName,

        @JsonProperty("total_value_eur")
        BigDecimal totalValueEur,

        @JsonProperty("absorbed_value_eur")
        BigDecimal absorbedValueEur,

        @JsonProperty("projects_count")
        Long projectsCount,

        @JsonProperty("percentage_of_total_pnrr")
        BigDecimal percentageOfTotalPnrr
) {
}