package com.dm.debtease.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "creditor")
@Table(name = "creditor", schema = "public")
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

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    CustomUser user;

    @OneToOne()
    @JoinColumn(name = "company_id")
    Company company;
}
