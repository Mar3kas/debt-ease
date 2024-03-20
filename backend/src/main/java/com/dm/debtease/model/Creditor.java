package com.dm.debtease.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "creditor")
@Table(name = "creditor")
public class Creditor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "address", nullable = false)
    String address;

    @Column(name = "phone_number", nullable = false)
    String phoneNumber;

    @Column(name = "email", nullable = false)
    String email;

    @Column(name = "account_number", nullable = false)
    String accountNumber;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    CustomUser user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    CompanyInformation company;
}
