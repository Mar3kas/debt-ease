package com.dm.debtease.repository;

import com.dm.debtease.model.DebtCaseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DebtCaseTypeRepository extends JpaRepository<DebtCaseType, Integer> {
}
