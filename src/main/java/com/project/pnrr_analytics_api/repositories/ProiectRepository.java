package com.project.pnrr_analytics_api.repositories;


import com.project.pnrr_analytics_api.dtos.*;
import com.project.pnrr_analytics_api.entities.EProiect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

// ... importuri

@Repository
public interface ProiectRepository extends JpaRepository<EProiect, UUID> {

    // --- IDEEA 1: KPI Summary ---
    // Returnăm FinancialStatsDto (3 câmpuri) pentru că DB-ul nu a calculat încă procentul
    @Query("""
        SELECT new com.pnrr.dashboard.dto.FinancialStatsDto(
            COALESCE(SUM(p.valoareEur), 0),
            COALESCE(SUM(p.absorbtieFinanciaraEur), 0),
            COUNT(p)
        )
        FROM Proiect p
    """)
    FinancialStatsDto getGeneralStats();

    // --- IDEEA 2: Geo Distribution ---
    @Query("""
        SELECT new com.pnrr.dashboard.dto.GeoDistributionDto(
            l.judet,
            l.regiune,
            COALESCE(SUM(p.valoareEur), 0),
            COUNT(p)
        )
        FROM Proiect p
        JOIN p.locatie l
        GROUP BY l.judet, l.regiune
        ORDER BY SUM(p.valoareEur) DESC
    """)
    List<GeoDistributionDto> getGeoDistribution();

    // --- IDEEA 3: Top Beneficiari ---
    // Observă parametrul 'Pageable pageable' la final.
    // Query-ul face JOIN, GROUP BY și ORDER BY suma descrescător.
    @Query("""
        SELECT new com.pnrr.dashboard.dto.TopBeneficiaryDto(
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
        SELECT new com.pnrr.dashboard.dto.CriRawStatsDto(
            i.cod,
            i.denumire,
            COALESCE(SUM(p.valoareEur), 0),
            COALESCE(SUM(p.absorbtieFinanciaraEur), 0),
            COUNT(p)
        )
        FROM Proiect p
        JOIN p.institutie i
        GROUP BY i.cod, i.denumire
        ORDER BY SUM(p.absorbtieFinanciaraEur) DESC
    """)
    List<CriRawStatsDto> getCriRawStats();

    // --- IDEEA 5: Progres Tehnic vs Financiar ---
    // Selectăm doar proiectele relevante (cu buget alocat)
    @Query("""
        SELECT new com.pnrr.dashboard.dto.ProjectProgressRawDto(
            p.id,
            p.titlu,
            COALESCE(p.progresTehnic, 0),
            COALESCE(p.progresFinanciar, 0),
            COALESCE(p.diferentaTehnicFinanciar, 0),
            p.valoareEur,
            b.nume
        )
        FROM Proiect p
        JOIN p.beneficiar b
        WHERE p.valoareEur > 0
    """)
    List<ProjectProgressRawDto> getProgressCorrelationRaw();

    // --- IDEEA 6: Structura Finanțării ---
    @Query("""
        SELECT new com.pnrr.dashboard.dto.FundingRawDto(
            COALESCE(p.sursaFinantare, 'NECUNOSCUT'),
            COALESCE(SUM(p.valoareEur), 0),
            COUNT(p)
        )
        FROM Proiect p
        GROUP BY p.sursaFinantare
    """)
    List<FundingRawDto> getFundingStructureRaw();
}