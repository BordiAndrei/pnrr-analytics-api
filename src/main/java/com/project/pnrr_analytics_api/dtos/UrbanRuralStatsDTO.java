package com.project.pnrr_analytics_api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
public record UrbanRuralStatsDTO(
        @JsonProperty("medium_type")
        String mediumType, // URBAN sau RURAL

        @JsonProperty("total_value_eur")
        BigDecimal totalValueEur,

        @JsonProperty("projects_count")
        Long projectsCount,

        @JsonProperty("avg_value_per_project")
        BigDecimal avgValuePerProject
) {
}