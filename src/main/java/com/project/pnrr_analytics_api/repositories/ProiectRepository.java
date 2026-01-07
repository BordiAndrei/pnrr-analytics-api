package com.project.pnrr_analytics_api.repositories;


import com.project.pnrr_analytics_api.entities.EProiect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface ProiectRepository extends JpaRepository<EProiect, UUID> {

    // Definim un record local mic pentru rezultatul agregării
    record FinancialStats(BigDecimal totalContractat, BigDecimal totalAbsorbit, Long count) {}

    // JPQL Query: Face suma direct în baza de date.
    // Gestionăm cazul NULL cu COALESCE (dacă nu sunt proiecte, returnăm 0).
    @Query("""
        SELECT new com.pnrr.dashboard.repository.ProiectRepository.FinancialStats(
            COALESCE(SUM(p.valoareEur), 0),
            COALESCE(SUM(p.absorbtieFinanciaraEur), 0),
            COUNT(p)
        )
        FROM Proiect p
    """)
    FinancialStats getGeneralStats();
}