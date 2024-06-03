package com.dm.debtease.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "debt_case_type")
@Table(name = "debt_case_type", schema = "public")
public class DebtCaseType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "type", nullable = false)
    String type;
}
