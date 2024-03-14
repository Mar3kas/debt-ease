package com.dm.debtease.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity(name = "company")
@Table(name = "company")
public class CompanyInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "industry", nullable = false)
    String industry;

    @Column(name = "domain", nullable = false)
    String domain;

    @Column(name = "locality", nullable = false)
    String locality;

    @Column(name = "country", nullable = false)
    @JsonIgnore
    String country;
}
