package com.project.pnrr_analytics_api.controllers;

import com.project.pnrr_analytics_api.dtos.KpiSummaryDto;
import com.project.pnrr_analytics_api.services.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/kpi-summary")
    public ResponseEntity<KpiSummaryDto> getKpiSummary() {
        var kpiData = analyticsService.getKpiSummary();

        return ResponseEntity.ok(kpiData);
    }
}