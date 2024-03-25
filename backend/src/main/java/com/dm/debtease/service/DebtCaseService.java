package com.dm.debtease.service;

import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.dto.DebtCaseDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DebtCaseService {
    List<DebtCase> getAllDebtCases();

    DebtCase getDebtCaseById(int id);

    List<DebtCase> getDebtCasesByCreditorUsername(String username);

    List<DebtCase> getDebtCasesByDebtorUsername(String username);

    DebtCase editDebtCaseByIdAndCreditorId(DebtCaseDTO debtCaseDTO, int id, int creditorId);

    boolean deleteDebtCaseByIdAndCreditorId(int id, int creditorId);

    Optional<DebtCase> findExistingDebtCase(String username, String... indicator);

    String getTypeToMatch(String type);

    boolean isDebtCasePending(DebtCase debtCase, LocalDateTime startTime, LocalDateTime endTime);
}
