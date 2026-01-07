package com.project.pnrr_analytics_api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "locatii")
@Getter
@Setter
public class ELocatie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "judet", nullable = false)
    private String judet;

    @Column(name = "localitate", nullable = false)
    private String localitate;

    @Column(name = "regiune")
    private String regiune; // Ex: Nord-Vest, Centru
}