package com.dm.debtease.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "custom_user")
@Table(name = "custom_user", schema = "public")
public class CustomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    int id;

    @Column(name = "username", nullable = false, unique = true)
    String username;

    @Column(name = "password", nullable = false)
    @JsonIgnore
    String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    Role role;
}
