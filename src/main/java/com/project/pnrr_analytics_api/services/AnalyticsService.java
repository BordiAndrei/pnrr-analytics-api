package com.project.pnrr_analytics_api.services;

import com.project.pnrr_analytics_api.dtos.*;
import com.project.pnrr_analytics_api.entities.EProiect;
import com.project.pnrr_analytics_api.repositories.ProiectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    // Metoda pentru Ideea 4
    @Transactional(readOnly = true)
    public List<CriPerformanceDto> getCriPerformance() {
        // 1. Luăm datele brute
        List<CriRawStatsDto> rawStats = proiectRepository.getCriRawStats();

        // 2. Procesăm fiecare rând (Java Stream API)
        return rawStats.stream()
                .map(stat -> {
                    // Calcul procent
                    double rate = 0.0;
                    if (stat.totalAlocat().compareTo(BigDecimal.ZERO) > 0) {
                        rate = stat.totalAbsorbit()
                                .divide(stat.totalAlocat(), 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100))
                                .doubleValue();
                    }

                    // Determinare Status (Logică de Business)
                    String status;
                    if (rate >= 20.0) {
                        status = "AHEAD";
                    } else if (rate >= 10.0) {
                        status = "ON_TRACK";
                    } else {
                        status = "LAGGING"; // Întârziat
                    }

                    // Construim DTO-ul final
                    return new CriPerformanceDto(
                            stat.cod(),
                            stat.denumire(),
                            stat.totalAlocat(),
                            stat.totalAbsorbit(),
                            rate,
                            stat.numarProiecte(),
                            status
                    );
                })
                .toList();
    }

    // Metoda pentru Ideea 5
    @Transactional(readOnly = true)
    public List<ProgressCorrelationDto> getProgressCorrelation() {
        // 1. Luăm datele brute
        List<ProjectProgressRawDto> rawProjects = proiectRepository.getProgressCorrelationRaw();

        // 2. Calculăm Risk Flag pentru fiecare proiect
        return rawProjects.stream()
                .map(p -> {
                    BigDecimal gap = p.diferentaTehnicFinanciar();
                    // Gap pozitiv = Tehnic > Financiar (Constructorul a muncit, Statul nu a plătit)
                    // Gap negativ = Financiar > Tehnic (Avansuri mari)

                    String riskFlag = "LOW";
                    double gapValue = Math.abs(gap.doubleValue());

                    if (gapValue > 15.0) {
                        riskFlag = "HIGH";
                    } else if (gapValue > 5.0) {
                        riskFlag = "MODERATE";
                    }

                    return new ProgressCorrelationDto(
                            p.id(),
                            p.titlu(),
                            p.progresTehnic(),
                            p.progresFinanciar(),
                            gap,
                            p.valoareEur(),
                            p.numeBeneficiar(),
                            riskFlag
                    );
                })
                .toList();
    }

    // Metoda pentru Ideea 6
    @Transactional(readOnly = true)
    public List<FundingStructureDto> getFundingStructure() {
        // 1. Luăm datele brute
        List<FundingRawDto> rawList = proiectRepository.getFundingStructureRaw();

        // 2. Calculăm Totalul General (Grand Total) în memorie
        BigDecimal grandTotal = rawList.stream()
                .map(FundingRawDto::totalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Mapăm la DTO-ul final cu calculul procentual
        return rawList.stream()
                .map(item -> {
                    double share = 0.0;
                    // Evităm împărțirea la zero
                    if (grandTotal.compareTo(BigDecimal.ZERO) > 0) {
                        share = item.totalValue()
                                .divide(grandTotal, 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100))
                                .doubleValue();
                    }

                    return new FundingStructureDto(
                            item.fundingType(),
                            item.totalValue(),
                            share,
                            item.projectCount()
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectBottleneckDTO> getBottleneckProjects(double thresholdVal) {
        BigDecimal threshold = BigDecimal.valueOf(thresholdVal);

        // Luăm top 50 proiecte cu probleme
        List<EProiect> projects = proiectRepository.findProjectsWithGap(threshold, PageRequest.of(0, 50));

        return projects.stream()
                .map(this::mapToBottleneckDTO)
                .toList(); // Java 16+ feature
    }

    @Transactional(readOnly = true)
    private ProjectBottleneckDTO mapToBottleneckDTO(EProiect p) {
        // 1. Gestionare Null Safety pentru Progres (poate veni null din DB)
        // Dacă e null, considerăm 0
        BigDecimal tech = p.getProgresTehnic() != null ? p.getProgresTehnic() : BigDecimal.ZERO;
        BigDecimal fin = p.getProgresFinanciar() != null ? p.getProgresFinanciar() : BigDecimal.ZERO;

        // 2. Calcul Gap
        BigDecimal gap = tech.subtract(fin);

        // 3. Calcul Suma Blocată: (Valoare * Gap) / 100
        BigDecimal valoare = p.getValoareEur() != null ? p.getValoareEur() : BigDecimal.ZERO;

        BigDecimal blockedAmount = valoare
                .multiply(gap)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // 4. Calcul Zile de la ultima actualizare
        long daysSince = 0;
        if (p.getDataActualizare() != null) {
            daysSince = ChronoUnit.DAYS.between(p.getDataActualizare(), LocalDateTime.now());
        }

        // 5. Safe Location
        // Observ în poza ta că relația e definită ca `p.getLocatie()`.
        String locationName = (p.getLocatie() != null) ? p.getLocatie().getJudet() : "Național/Necunoscut";

        return new ProjectBottleneckDTO(
                p.getId(),
                p.getTitlu(),
                gap,
                tech,
                fin,
                blockedAmount,
                p.getBeneficiar().getNume(), // Asumând că getBeneficiar() nu e null (schema zice NOT NULL)
                locationName,
                daysSince
        );
    }

    @Transactional(readOnly = true)
    public List<ComponentBreakdownDTO> getComponentsBreakdown() {
        // 1. Obținem datele agregate din DB
        List<ComponentRawStatsDto> rawStats = proiectRepository.getRawComponentStats();

        // 2. Calculăm Totalul General (Grand Total) pentru a afla procentele
        // Folosim Stream API pentru a suma toate 'totalValue'
        BigDecimal grandTotalEur = rawStats.stream()
                .map(stat -> stat.totalValue() != null ? stat.totalValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Evităm împărțirea la zero
        if (grandTotalEur.compareTo(BigDecimal.ZERO) == 0) {
            return List.of();
        }

        // 3. Mapăm către DTO-ul final cu calculul procentual
        return rawStats.stream()
                .map(stat -> {
                    BigDecimal total = stat.totalValue() != null ? stat.totalValue() : BigDecimal.ZERO;
                    BigDecimal absorbed = stat.absorbedValue() != null ? stat.absorbedValue() : BigDecimal.ZERO;

                    // Calcul procent: (Total Componentă / Grand Total) * 100
                    BigDecimal percentage = total
                            .multiply(BigDecimal.valueOf(100))
                            .divide(grandTotalEur, 2, RoundingMode.HALF_UP);

                    return new ComponentBreakdownDTO(
                            stat.code(),
                            stat.name(),
                            total,
                            absorbed,
                            stat.count(),
                            percentage
                    );
                })
                .toList();
    }
}