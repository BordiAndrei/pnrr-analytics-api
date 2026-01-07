package com.project.pnrr_analytics_api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.UUID;
public record ProjectBottleneckDTO(
        @JsonProperty("project_id") UUID projectId,
        @JsonProperty("project_title") String projectTitle,
        @JsonProperty("gap_percent") BigDecimal gapPercent,
        @JsonProperty("tech_progress") BigDecimal techProgress,
        @JsonProperty("fin_progress") BigDecimal finProgress,
        @JsonProperty("blocked_amount_estimated") BigDecimal blockedAmountEstimated,
        @JsonProperty("beneficiary") String beneficiaryName,
        @JsonProperty("location") String locationName,
        @JsonProperty("days_since_last_update") Long daysSinceLastUpdate
) {}