package com.project.pnrr_analytics_api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "masuri")
@Getter
@Setter
public class EMasura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String cod; // ex: I1, R2

    @Column(columnDefinition = "TEXT")
    private String denumire;

    // Relația cu Componenta (Many-to-One)
    // O măsură aparține unei singure componente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_componenta", nullable = false)
    private EComponenta componenta;
}