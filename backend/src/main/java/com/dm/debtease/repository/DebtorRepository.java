package com.dm.debtease.repository;

import com.dm.debtease.model.Debtor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DebtorRepository extends JpaRepository<Debtor, Integer> {
    Optional<Debtor> findByNameAndSurname(String name, String surname);
    Optional<Debtor> findByUserUsername(String username);
}
