package com.project.pnrr_analytics_api.repositories;


import com.project.pnrr_analytics_api.dtos.FinancialStatsDto;
import com.project.pnrr_analytics_api.dtos.GeoDistributionDto;
import com.project.pnrr_analytics_api.entities.EProiect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}