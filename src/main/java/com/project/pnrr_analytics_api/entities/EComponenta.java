package com.project.pnrr_analytics_api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "componente")
@Getter
@Setter
public class EComponenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String cod; // ex: C1, C7

    @Column(nullable = false)
    private String denumire; // ex: Managementul apei
}