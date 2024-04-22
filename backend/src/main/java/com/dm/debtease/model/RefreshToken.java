package com.dm.debtease.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "refresh_token")
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "token", nullable = false)
    String token;

    @Column(name = "expiration_date", nullable = false)
    Instant expirationDate;

    @OneToOne()
    @JoinColumn(name = "user_id", nullable = false)
    CustomUser user;
}
