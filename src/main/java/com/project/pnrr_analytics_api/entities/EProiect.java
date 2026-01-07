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

    // FetchType.LAZY este "Best Practice" de senior.
    // Nu vrem să încărcăm locația automat de fiecare dată când citim un proiect,
    // decât dacă o cerem explicit (performanță).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_locatie")
    private ELocatie locatie;

    // --- RELAȚIA NOUĂ (Ideea 3) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_beneficiar", nullable = false) // FK definit în DB
    private EBeneficiar beneficiar;

    // --- RELAȚIA NOUĂ (Ideea 4) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_institutie", nullable = false)
    private EInstitutie institutie;
}