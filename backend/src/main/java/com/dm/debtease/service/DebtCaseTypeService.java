package com.dm.debtease.service;

import com.dm.debtease.model.DebtCaseType;

import java.util.List;

public interface DebtCaseTypeService {
    List<DebtCaseType> getAllDebtCaseTypes();

    DebtCaseType getDebtCaseTypeById(int id);

    DebtCaseType findMatchingDebtCaseType(String typeToMatch);

    DebtCaseType getDefaultDebtCaseType();
}
