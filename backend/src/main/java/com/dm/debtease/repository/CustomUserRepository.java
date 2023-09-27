package com.dm.debtease.repository;

import com.dm.debtease.model.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomUserRepository extends JpaRepository<CustomUser, Integer> {
    Optional<CustomUser> findByUsername(String username);
}
