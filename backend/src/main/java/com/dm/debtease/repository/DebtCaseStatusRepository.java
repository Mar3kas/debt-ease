package com.dm.debtease.repository;

import com.dm.debtease.model.DebtCaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DebtCaseStatusRepository extends JpaRepository<DebtCaseStatus, Integer> {
}
