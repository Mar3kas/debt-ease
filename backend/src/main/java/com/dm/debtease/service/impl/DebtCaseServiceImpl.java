package com.dm.debtease.service.impl;

import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.DebtCaseStatus;
import com.dm.debtease.model.DebtPaymentStrategy;
import com.dm.debtease.model.dto.DebtCaseDTO;
import com.dm.debtease.model.dto.DebtPaymentStrategyDTO;
import com.dm.debtease.model.dto.PaymentRequestDTO;
import com.dm.debtease.repository.DebtCaseRepository;
import com.dm.debtease.service.DebtCaseService;
import com.dm.debtease.service.DebtCaseTypeService;
import com.dm.debtease.utils.Constants;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DebtCaseServiceImpl implements DebtCaseService {
    private final DebtCaseRepository debtCaseRepository;
    private final DebtCaseTypeService debtCaseTypeService;

    @Override
    public List<DebtCase> getAllDebtCases() {
        return debtCaseRepository.findAll();
    }

    @Override
    public DebtCase getDebtCaseById(int id) {
        Optional<DebtCase> optionalDebtCase = debtCaseRepository.findById(id);
        return optionalDebtCase.orElseThrow(
                () -> new EntityNotFoundException(String.format(Constants.DEBT_CASE_NOT_FOUND, id)));
    }

    @Override
    public List<DebtCase> getDebtCasesByCreditorUsername(String username) {
        return debtCaseRepository.findByCreditor_User_Username(username);
    }

    @Override
    public List<DebtCase> getDebtCasesByDebtorUsername(String username) {
        return debtCaseRepository.findByDebtor_User_Username(username);
    }

    @Override
    public DebtCase editDebtCaseByIdAndCreditorId(DebtCaseDTO debtCaseDTO, int id, int creditorId) {
        Optional<DebtCase> optionalDebtCase = debtCaseRepository.findByIdAndCreditor_Id(id, creditorId);
        if (optionalDebtCase.isPresent()) {
            DebtCase debtCase = optionalDebtCase.get();
            if (debtCaseDTO.getDueDate() != null) {
                debtCase.setDueDate(debtCaseDTO.getDueDate());
            }
            if (debtCaseDTO.getTypeId() > 0) {
                debtCase.setDebtCaseType(debtCaseTypeService.getDebtCaseTypeById(debtCaseDTO.getTypeId()));
            }
            debtCase.setModifiedDate(LocalDateTime.now());
            return debtCaseRepository.save(debtCase);
        }
        throw new EntityNotFoundException(
                String.format(Constants.DEBT_CASE_NOT_FOUND_WITH_ID_CREDITOR_ID, id, creditorId));
    }

    @Override
    public boolean deleteDebtCaseByIdAndCreditorId(int id, int creditorId) {
        Optional<DebtCase> optionalDebtCase = debtCaseRepository.findByIdAndCreditor_Id(id, creditorId);
        if (optionalDebtCase.isPresent()) {
            debtCaseRepository.deleteById(id);
            return true;
        }
        throw new EntityNotFoundException(
                String.format(Constants.DEBT_CASE_NOT_FOUND_WITH_ID_CREDITOR_ID, id, creditorId));
    }

    @Override
    public Optional<DebtCase> findExistingDebtCase(String username, String... indicator) {
        return debtCaseRepository.findByAmountOwedAndDueDateAndDebtCaseType_TypeAndCreditor_User_UsernameAndDebtor_NameAndDebtor_Surname(
                new BigDecimal(indicator[0]),
                LocalDateTime.parse(indicator[1], Constants.DATE_TIME_FORMATTER),
                debtCaseTypeService.getTypeToMatch(indicator[2]),
                username,
                indicator[3],
                indicator[4]
        );
    }

    @Override
    public boolean isDebtCasePending(DebtCase debtCase, LocalDateTime startTime, LocalDateTime endTime) {
        return debtCase.getDueDate().isAfter(startTime)
                && debtCase.getDueDate().isBefore(endTime)
                && !DebtCaseStatus.CLOSED.equals(debtCase.getDebtCaseStatus());
    }

    @Override
    public DebtCase updateDebtCaseAfterPayment(DebtCase debtCase, PaymentRequestDTO paymentRequestDTO) {
        BigDecimal newAmountOwed = debtCase.getAmountOwed();
        DebtCaseStatus newStatus = DebtCaseStatus.UNPAID;
        if (!paymentRequestDTO.getIsPaymentInFull()) {
            newAmountOwed = getValidLeftAmountOwed(paymentRequestDTO.getPaymentAmount(), newAmountOwed);
        } else {
            newStatus = DebtCaseStatus.CLOSED;
            newAmountOwed = BigDecimal.ZERO;
        }
        debtCase.setAmountOwed(newAmountOwed);
        debtCase.setDebtCaseStatus(newStatus);
        return debtCaseRepository.save(debtCase);
    }

    @Override
    public BigDecimal getValidLeftAmountOwed(BigDecimal paymentAmount, BigDecimal currentAmountOwed) {
        return paymentAmount.compareTo(currentAmountOwed) > 0 ?
                currentAmountOwed :
                currentAmountOwed.subtract(paymentAmount);
    }

    @Override
    public DebtPaymentStrategy calculateDebtPaymentStrategies(DebtPaymentStrategyDTO debtPaymentStrategyDTO,
                                                              String username) {
        List<DebtCase> debtCases = getDebtCasesByDebtorUsername(username);
        if (!debtCases.isEmpty())
        {
            debtCases = new ArrayList<>(filterClosedDebtCases(debtCases));
            DebtPaymentStrategy debtPaymentStrategy = new DebtPaymentStrategy();

            debtCases.sort(Comparator.comparing(DebtCase::getAmountOwed));
            List<BigDecimal> snowballStrategyResult =  calculatePaymentStrategyUntilPayedOff(debtPaymentStrategyDTO, debtCases);
            debtPaymentStrategy.setSnowballBalanceEachMonth(snowballStrategyResult);

            debtCases.sort(Comparator.comparing(DebtCase::getDebtInterestRate).reversed());
            List<BigDecimal> avalancheStrategyResult = calculatePaymentStrategyUntilPayedOff(debtPaymentStrategyDTO, debtCases);
            debtPaymentStrategy.setAvalancheBalanceEachMonth(avalancheStrategyResult);

            return debtPaymentStrategy;
        }
        throw new EntityNotFoundException(
                String.format(Constants.DEBT_CASES_EMPTY, username));
    }

    private List<BigDecimal> calculatePaymentStrategyUntilPayedOff(DebtPaymentStrategyDTO debtPaymentStrategyDTO,
                                                                   List<DebtCase> debtCases) {
        List<BigDecimal> strategyBalanceEachMonth = new ArrayList<>();
        List<DebtCase> tempDebtCases = new ArrayList<>(debtCases.stream().map(DebtCase::new).toList());
        BigDecimal totalDebt = calculateTotalDebt(tempDebtCases);
        strategyBalanceEachMonth.add(totalDebt);
        BigDecimal extraPaymentForSmallestDebt = debtPaymentStrategyDTO.getExtraMonthlyPaymentForHighestDebt();
        BigDecimal minimalMonthlyPaymentForEachDebt = debtPaymentStrategyDTO.getMinimalMonthlyPaymentForEachDebt();
        while (totalDebt.compareTo(BigDecimal.ZERO) > 0) {
            for (int i = 0; i < tempDebtCases.size(); i++) {
                DebtCase debt = tempDebtCases.get(i);
                BigDecimal amountOwedWithInterestRate =
                        debt.getAmountOwed().multiply(BigDecimal.valueOf((debt.getDebtInterestRate() / 12) / 100))
                                .setScale(2, RoundingMode.HALF_UP);
                debt.setAmountOwed(debt.getAmountOwed().add(amountOwedWithInterestRate));
                totalDebt = totalDebt.add(amountOwedWithInterestRate);
                if (i == 0) {
                    BigDecimal extraPayment = extraPaymentForSmallestDebt.compareTo(debt.getAmountOwed()) < 0 ?
                            extraPaymentForSmallestDebt : debt.getAmountOwed();
                    debt.setAmountOwed(debt.getAmountOwed().subtract(extraPayment));
                    totalDebt = totalDebt.subtract(extraPayment);
                } else {
                    BigDecimal minimalPayment = minimalMonthlyPaymentForEachDebt.compareTo(debt.getAmountOwed()) < 0 ?
                            minimalMonthlyPaymentForEachDebt : debt.getAmountOwed();
                    debt.setAmountOwed(debt.getAmountOwed().subtract(minimalPayment));
                    totalDebt = totalDebt.subtract(minimalPayment);
                }
                if (debt.getAmountOwed().compareTo(BigDecimal.ZERO) <= 0) {
                    tempDebtCases.remove(debt);
                    i--;
                }
            }
            strategyBalanceEachMonth.add(totalDebt);
        }
        return strategyBalanceEachMonth;
    }

    private BigDecimal calculateTotalDebt(List<DebtCase> debtCases) {
        return debtCases.stream()
                .map(DebtCase::getAmountOwed)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<DebtCase> filterClosedDebtCases(List<DebtCase> debtCases) {
        return debtCases.stream()
                .filter(debtCase -> !DebtCaseStatus.CLOSED.equals(debtCase.getDebtCaseStatus()))
                .toList();
    }
}
