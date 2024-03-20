package com.dm.debtease.service.impl;

import com.dm.debtease.model.Creditor;
import com.dm.debtease.model.CustomUser;
import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.Debtor;
import com.dm.debtease.model.dto.DebtCaseDTO;
import com.dm.debtease.repository.DebtCaseRepository;
import com.dm.debtease.repository.DebtCaseTypeRepository;
import com.dm.debtease.service.DebtCaseService;
import com.dm.debtease.utils.Constants;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DebtCaseServiceImpl implements DebtCaseService {
    private final DebtCaseRepository debtCaseRepository;
    private final DebtCaseTypeRepository debtCaseTypeRepository;

    @Override
    public List<DebtCase> getAllDebtCases() {
        return debtCaseRepository.findAll();
    }

    @Override
    public DebtCase getDebtCaseById(int id) {
        Optional<DebtCase> optionalDebtCase = debtCaseRepository.findById(id);
        return optionalDebtCase.orElseThrow(() -> new EntityNotFoundException(String.format(Constants.DEBT_CASE_NOT_FOUND, id)));
    }

    @Override
    public List<DebtCase> getDebtCasesByCreditorUsername(String username) {
        List<DebtCase> debtCases = debtCaseRepository.findAll();
        return debtCases.stream()
                .filter(debtCase -> {
                    Creditor creditor = debtCase.getCreditor();
                    if (creditor != null) {
                        CustomUser user = creditor.getUser();
                        return user != null && Objects.equals(user.getUsername(), username);
                    }
                    return false;
                })
                .toList();
    }

    @Override
    public List<DebtCase> getDebtCasesByDebtorUsername(String username) {
        List<DebtCase> debtCases = debtCaseRepository.findAll();
        return debtCases.stream()
                .filter(debtCase -> {
                    Debtor debtor = debtCase.getDebtor();
                    if (debtor != null) {
                        CustomUser user = debtor.getUser();
                        return user != null && Objects.equals(user.getUsername(), username);
                    }
                    return false;
                })
                .toList();
    }

    @Override
    public DebtCase editDebtCaseById(DebtCaseDTO debtCaseDTO, int id, int creditorId) {
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
                debtCase.setDebtCaseType(debtCaseTypeRepository.findById(debtCaseDTO.getTypeId())
                        .orElseThrow(() -> new EntityNotFoundException(String.format(Constants.DEBT_CASE_TYPE_NOT_FOUND, debtCaseDTO.getTypeId()))));
            }
            debtCase.setModifiedDate(LocalDateTime.parse(LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER)));
            return debtCaseRepository.save(debtCase);
        }
        throw new EntityNotFoundException(String.format(Constants.DEBT_CASE_NOT_FOUND, id));
    }

    @Override
    public boolean deleteDebtCaseById(int id, int creditorId) {
        Optional<DebtCase> optionalDebtCase = debtCaseRepository.findByIdAndCreditor_Id(id, creditorId);
        if (optionalDebtCase.isPresent()) {
            debtCaseRepository.deleteById(id);
            return true;
        }
        throw new EntityNotFoundException(String.format(Constants.DEBT_CASE_NOT_FOUND, id));
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
        return type.toUpperCase().contains("_DEBT") ? type.toUpperCase() : type.toUpperCase().concat("_DEBT");
    }
}
