package com.project.pnrr_analytics_api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;
@Entity
@Table(name = "proiecte")
@Getter
@Setter
public class EProiect {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "titlu", nullable = false)
    private String titlu;

    // Mapăm doar câmpurile necesare pentru KPI-uri acum,
    // dar entity-ul ar trebui să le aibă pe toate conform DB.

    @Column(name = "valoare_eur")
    private BigDecimal valoareEur;

    @Column(name = "absorbtie_financiara_eur")
    private BigDecimal absorbtieFinanciaraEur;

    // Putem adăuga restul câmpurilor (instituție, beneficiar etc.) pe parcurs
}