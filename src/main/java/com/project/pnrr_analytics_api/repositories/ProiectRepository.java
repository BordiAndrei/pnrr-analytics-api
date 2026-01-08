package com.project.pnrr_analytics_api.repositories;


import com.project.pnrr_analytics_api.dtos.*;
import com.project.pnrr_analytics_api.entities.EProiect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

// ... importuri

@Repository
public interface ProiectRepository extends JpaRepository<EProiect, UUID> {

    // --- IDEEA 1: KPI Summary ---
    // Returnăm FinancialStatsDto (3 câmpuri) pentru că DB-ul nu a calculat încă procentul
    @Query("""
        SELECT new com.project.pnrr_analytics_api.dtos.FinancialStatsDto(
            COALESCE(SUM(p.valoareEur), 0),
            COALESCE(SUM(p.absorbtieFinanciaraEur), 0),
            COUNT(p)
        )
        FROM EProiect p
    """)
    FinancialStatsDto getGeneralStats();

    // --- IDEEA 2: Geo Distribution ---
    @Query("""
        SELECT new com.project.pnrr_analytics_api.dtos.GeoDistributionDto(
            l.judet,
            l.regiune,
            COALESCE(SUM(p.valoareEur), 0),
            COUNT(p)
        )
        FROM EProiect p
        JOIN p.locatie l
        GROUP BY l.judet, l.regiune
        ORDER BY SUM(p.valoareEur) DESC
    """)
    List<GeoDistributionDto> getGeoDistribution();

    // --- IDEEA 3: Top Beneficiari ---
    // Observă parametrul 'Pageable pageable' la final.
    // Query-ul face JOIN, GROUP BY și ORDER BY suma descrescător.
    @Query("""
        SELECT new com.project.pnrr_analytics_api.dtos.TopBeneficiaryDto(
            b.nume,
            b.cui,
            b.tip,
            COALESCE(SUM(p.valoareEur), 0),
            COUNT(p)
        )
        FROM Proiect p
        JOIN p.beneficiar b
        GROUP BY b.nume, b.cui, b.tip
        ORDER BY SUM(p.valoareEur) DESC
    """)
    List<TopBeneficiaryDto> getTopBeneficiaries(Pageable pageable);

    // --- IDEEA 4: Performanța CRI ---
    @Query("""
        SELECT new com.project.pnrr_analytics_api.dtos.CriRawStatsDto(
            i.cod,
            i.denumire,
            COALESCE(SUM(p.valoareEur), 0),
            COALESCE(SUM(p.absorbtieFinanciaraEur), 0),
            COUNT(p)
        )
        FROM EProiect p
        JOIN p.institutie i
        GROUP BY i.cod, i.denumire
        ORDER BY SUM(p.absorbtieFinanciaraEur) DESC
    """)
    List<CriRawStatsDto> getCriRawStats();

    // --- IDEEA 5: Progres Tehnic vs Financiar ---
    // Selectăm doar proiectele relevante (cu buget alocat)
    @Query("""
        SELECT new com.project.pnrr_analytics_api.dtos.ProjectProgressRawDto(
            p.id,
            p.titlu,
            COALESCE(p.progresTehnic, 0),
            COALESCE(p.progresFinanciar, 0),
            COALESCE(p.diferentaTehnicFinanciar, 0),
            p.valoareEur,
            b.nume
        )
        FROM EProiect p
        JOIN p.beneficiar b
        WHERE p.valoareEur > 0
    """)
    List<ProjectProgressRawDto> getProgressCorrelationRaw();

    // --- IDEEA 6: Structura Finanțării ---
    @Query("""
        SELECT new com.project.pnrr_analytics_api.dtos.FundingRawDto(
            COALESCE(p.sursaFinantare, 'NECUNOSCUT'),
            COALESCE(SUM(p.valoareEur), 0),
            COUNT(p)
        )
        FROM EProiect p
        GROUP BY p.sursaFinantare
    """)
    List<FundingRawDto> getFundingStructureRaw();

    /**
     * Selectează proiectele unde diferența dintre progresul tehnic și cel financiar
     * depășește un anumit prag (threshold).
     */
    @Query("""
        SELECT new com.project.pnrr_analytics_api.dtos.ProjectBottleneckDTO(
            p.id,
            p.titlu,
            (p.progresTehnic - p.progresFinanciar),
            p.progresTehnic,
            p.progresFinanciar,
            (p.valoareEur * (p.progresTehnic - p.progresFinanciar) / 100),
            b.nume,
            l.judet,
            0L -- Vom calcula zilele în Service, aici punem placeholder sau folosim logică SQL complexă
        )
        FROM EProiect p
        JOIN p.beneficiar b
        LEFT JOIN p.locatie l
        WHERE (p.progresTehnic - p.progresFinanciar) > :threshold
        AND p.valoareEur > 0
        ORDER BY (p.progresTehnic - p.progresFinanciar) DESC
    """)
    List<ProjectBottleneckDTO> findBottleneckProjects(@Param("threshold") BigDecimal threshold, Pageable pageable);

    @Query("""
        SELECT p
        FROM EProiect p
        JOIN FETCH p.beneficiar
        LEFT JOIN FETCH p.locatie
        WHERE (p.progresTehnic - p.progresFinanciar) > :threshold
        AND p.valoareEur > 0
        ORDER BY (p.progresTehnic - p.progresFinanciar) DESC
    """)
    List<EProiect> findProjectsWithGap(@Param("threshold") BigDecimal threshold, Pageable pageable);

    // Ideea: 8
    @Query("""
        SELECT new com.project.pnrr_analytics_api.dtos.ComponentRawStatsDto(
            c.cod,
            c.denumire,
            SUM(p.valoareEur),
            SUM(p.absorbtieFinanciaraEur),
            COUNT(p)
        )
        FROM EProiect p
        JOIN p.masura m
        JOIN m.componenta c
        GROUP BY c.cod, c.denumire
    """)
    List<ComponentRawStatsDto> getRawComponentStats();

    // Ideea: 9
    @Query("""
        SELECT new com.project.pnrr_analytics_api.dtos.LocatieValoareDto(
            l.localitate,
            p.valoareEur
        )
        FROM EProiect p
        JOIN p.locatie l
        WHERE p.valoareEur > 0
    """)
    List<LocatieValoareDto> findAllProjectLocationsAndValues();

    // Ideea: 10
    @Query("""
        SELECT new com.project.pnrr_analytics_api.dtos.MonthlyAbsorptionRawDto(
            p.anRaportare,
            p.lunaRaportare,
            SUM(p.absorbtieFinanciaraEur)
        )
        FROM EProiect p
        WHERE p.anRaportare IS NOT NULL AND p.lunaRaportare IS NOT NULL
        GROUP BY p.anRaportare, p.lunaRaportare
    """)
    List<MonthlyAbsorptionRawDto> getMonthlyAbsorptionStats();
}