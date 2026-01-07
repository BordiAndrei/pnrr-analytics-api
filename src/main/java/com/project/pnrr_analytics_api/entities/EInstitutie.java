package com.project.pnrr_analytics_api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "institutii")
@Getter
@Setter
public class EInstitutie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cod", nullable = false, unique = true)
    private String cod; // Ex: MIPE, MTI, MEDAT

    @Column(name = "denumire", nullable = false)
    private String denumire;
}