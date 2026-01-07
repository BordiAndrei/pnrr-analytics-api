package com.project.pnrr_analytics_api.controllers;

import com.project.pnrr_analytics_api.dtos.*;
import com.project.pnrr_analytics_api.services.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/geo-distribution")
    public ResponseEntity<List<GeoDistributionDto>> getGeoDistribution() {
        var geoData = analyticsService.getGeoDistribution();
        return ResponseEntity.ok(geoData);
    }

    @GetMapping("/top-beneficiaries")
    public ResponseEntity<List<TopBeneficiaryDto>> getTopBeneficiaries() {
        var topBeneficiaries = analyticsService.getTopBeneficiaries();
        return ResponseEntity.ok(topBeneficiaries);
    }

    @GetMapping("/cri-performance")
    public ResponseEntity<List<CriPerformanceDto>> getCriPerformance() {
        return ResponseEntity.ok(analyticsService.getCriPerformance());
    }

    @GetMapping("/progress-correlation")
    public ResponseEntity<List<ProgressCorrelationDto>> getProgressCorrelation() {
        return ResponseEntity.ok(analyticsService.getProgressCorrelation());
    }
}