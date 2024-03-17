package com.dm.debtease.service;

import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.dto.DebtCaseDTO;

import java.util.List;
import java.util.Optional;

public interface DebtCaseService {
    List<DebtCase> getAllDebtCases();

    DebtCase getDebtCaseById(int id);

    List<DebtCase> getDebtCasesByCreditorUsername(String username);

    List<DebtCase> getDebtCasesByDebtorUsername(String username);

    DebtCase editDebtCaseById(DebtCaseDTO debtCaseDTO, int id, int creditorId);

    boolean deleteDebtCaseById(int id, int creditorId);

    void markDebtCaseEmailAsSentById(int id);

    Optional<DebtCase> findExistingDebtCase(String username, String... indicator);

    String getTypeToMatch(String type);
}
