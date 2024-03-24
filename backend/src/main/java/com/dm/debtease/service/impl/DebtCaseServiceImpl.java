package com.dm.debtease.service.impl;

import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.dto.DebtCaseDTO;
import com.dm.debtease.repository.DebtCaseRepository;
import com.dm.debtease.service.DebtCaseService;
import com.dm.debtease.service.DebtCaseTypeService;
import com.dm.debtease.utils.Constants;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
            if (debtCaseDTO.getAmountOwed() != null) {
                debtCase.setAmountOwed(debtCaseDTO.getAmountOwed());
            }
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
                getTypeToMatch(indicator[2]),
                username,
                indicator[3],
                indicator[4]
        );
    }

    @Override
    public String getTypeToMatch(String type) {
        if (type.isEmpty()) {
            return "DEFAULT_DEBT";
        }
        return type.toUpperCase().contains("_DEBT") ? type.toUpperCase() : type.toUpperCase().concat("_DEBT");
    }
}
