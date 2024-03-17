package com.dm.debtease.repository;

import com.dm.debtease.model.DebtCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DebtCaseRepository extends JpaRepository<DebtCase, Integer> {
    Optional<DebtCase> findByAmountOwedAndDueDateAndDebtCaseType_TypeAndCreditor_User_UsernameAndDebtor_NameAndDebtor_Surname(BigDecimal amountOwed, LocalDateTime dueDate, String type, String username, String name, String surname);

    Optional<DebtCase> findByIdAndCreditor_Id(int id, int creditorId);

    List<DebtCase> findByDueDateLessThanEqual(LocalDateTime dueDate);
}
