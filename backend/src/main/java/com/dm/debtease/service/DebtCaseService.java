package com.dm.debtease.service;

import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.DebtPaymentStrategy;
import com.dm.debtease.model.dto.DebtCaseDTO;
import com.dm.debtease.model.dto.DebtPaymentStrategyDTO;
import com.dm.debtease.model.dto.PaymentRequestDTO;

import java.math.BigDecimal;
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

    boolean isDebtCasePending(DebtCase debtCase, LocalDateTime startTime, LocalDateTime endTime);

    DebtCase updateDebtCaseAfterPayment(DebtCase debtCase, PaymentRequestDTO paymentRequestDTO);

    BigDecimal getValidLeftAmountOwed(BigDecimal paymentAmount, BigDecimal currentAmountOwed);

    DebtPaymentStrategy calculateDebtPaymentStrategies(DebtPaymentStrategyDTO debtPaymentStrategyDTO, String username);
}
