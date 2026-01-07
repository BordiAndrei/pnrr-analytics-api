package com.project.pnrr_analytics_api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    // --- CÂMPURILE LIPSĂ (Ideea 7) ---

    @Column(name = "progres_tehnic")
    private BigDecimal progresTehnic;

    @Column(name = "progres_financiar")
    private BigDecimal progresFinanciar;

    // Opțional: coloana calculată (dacă vrei să o mapzi, deși o calculăm și în service)
    @Column(name = "diferenta_tehnic_financiar")
    private BigDecimal diferentaTehnicFinanciar;

    @Column(name = "data_actualizare")
    private LocalDateTime dataActualizare;

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

    // --- RELAȚIA NOUĂ (Ideea 8) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_masura", nullable = false)
    private EMasura masura;
}