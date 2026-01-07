package com.project.pnrr_analytics_api.services;

import com.project.pnrr_analytics_api.dtos.FinancialStatsDto;
import com.project.pnrr_analytics_api.dtos.GeoDistributionDto;
import com.project.pnrr_analytics_api.dtos.KpiSummaryDto;
import com.project.pnrr_analytics_api.dtos.TopBeneficiaryDto;
import com.project.pnrr_analytics_api.repositories.ProiectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ProiectRepository proiectRepository;

    @Transactional(readOnly = true)
    public KpiSummaryDto getKpiSummary() {
        // 1. Luăm datele brute (3 câmpuri) folosind DTO-ul intermediar
        FinancialStatsDto stats = proiectRepository.getGeneralStats();

        // 2. Extragem valorile pentru calcul
        BigDecimal totalContractat = stats.totalContractat();
        BigDecimal totalAbsorbit = stats.totalAbsorbit();
        Long count = stats.count();

        // 3. Calculăm procentul (Logica de Business)
        double rataAbsorbtie = 0.0;
        if (totalContractat.compareTo(BigDecimal.ZERO) > 0) {
            rataAbsorbtie = totalAbsorbit
                    .divide(totalContractat, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        // 4. Returnăm DTO-ul TĂU final (care cere 4 câmpuri)
        return new KpiSummaryDto(
                totalContractat,
                totalAbsorbit,
                rataAbsorbtie,
                count
        );
    }

    @Transactional(readOnly = true)
    public List<GeoDistributionDto> getGeoDistribution() {
        // Apelăm repository-ul
        // Aici am putea adăuga logică extra, de ex: să calculăm procentul din total pentru fiecare județ.
        // Pentru moment, returnăm datele brute agregate.
        return proiectRepository.getGeoDistribution();
    }

    // Metoda pentru Ideea 3
    @Transactional(readOnly = true)
    public List<TopBeneficiaryDto> getTopBeneficiaries() {
        // Cerem pagina 0, cu dimensiunea 10.
        // Asta va genera LIMIT 10 în SQL.
        return proiectRepository.getTopBeneficiaries(PageRequest.of(0, 10));
    }
}