package com.dm.debtease.repository;

import com.dm.debtease.model.DebtCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DebtCaseRepository extends JpaRepository<DebtCase, Integer> {
}
