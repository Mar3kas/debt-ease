package com.dm.debtease.repository;

import com.dm.debtease.model.Creditor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreditorRepository extends JpaRepository<Creditor, Integer> {
    Optional<Creditor> findByUserUsername(String username);
}
