package com.project.pnrr_analytics_api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "beneficiari")
@Getter
@Setter
public class EBeneficiar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cui", unique = true)
    private String cui;

    @Column(name = "nume", nullable = false)
    private String nume;

    @Column(name = "tip")
    private String tip; // Ex: UAT, SRL, Companie Nationala
}