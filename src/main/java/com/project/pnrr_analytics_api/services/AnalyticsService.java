package com.project.pnrr_analytics_api.services;

import com.project.pnrr_analytics_api.dtos.KpiSummaryDto;
import com.project.pnrr_analytics_api.repositories.ProiectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ProiectRepository proiectRepository;

    @Transactional(readOnly = true) // Optimizare: spunem bazei de date că doar citim
    public KpiSummaryDto getKpiSummary() {
        // 1. Luăm datele brute din DB
        var stats = proiectRepository.getGeneralStats();

        // 2. Extragem variabilele (Java 21 'var' pentru inferența tipului)
        var totalContractat = stats.totalContractat();
        var totalAbsorbit = stats.totalAbsorbit();
        var count = stats.count();

        // 3. Logica de business: Calcul procent
        // Atenție: Evităm împărțirea la zero!
        double rataAbsorbtie = 0.0;

        if (totalContractat.compareTo(BigDecimal.ZERO) > 0) {
            // Formula: (Absorbit / Contractat) * 100
            rataAbsorbtie = totalAbsorbit
                    .divide(totalContractat, 4, RoundingMode.HALF_UP) // Precizie 4 zecimale
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        // 4. Returnăm DTO-ul final
        return new KpiSummaryDto(
                totalContractat,
                totalAbsorbit,
                rataAbsorbtie,
                count
        );
    }
}