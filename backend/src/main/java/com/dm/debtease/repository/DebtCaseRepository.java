package com.dm.debtease.repository;

import com.dm.debtease.model.DebtCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface DebtCaseRepository extends JpaRepository<DebtCase, Integer> {
    Optional<DebtCase> findByAmountOwedAndDueDateAndDebtCaseType_TypeAndCreditor_Id(BigDecimal amountOwed, LocalDateTime dueDate, String type, int id);
}
