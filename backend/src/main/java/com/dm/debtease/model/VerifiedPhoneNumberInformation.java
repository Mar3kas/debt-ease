package com.dm.debtease.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "verified_phone_number_information")
@Table(name = "verified_phone_number_information", schema = "public")
public class VerifiedPhoneNumberInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "valid", nullable = false)
    String valid;

    @Column(name = "location")
    String location;

    @Column(name = "carrier")
    String carrier;

    @Column(name = "line_type")
    String lineType;
}
