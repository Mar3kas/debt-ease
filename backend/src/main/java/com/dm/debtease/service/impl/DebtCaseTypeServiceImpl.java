package com.dm.debtease.service.impl;

import com.dm.debtease.model.DebtCaseType;
import com.dm.debtease.repository.DebtCaseTypeRepository;
import com.dm.debtease.service.DebtCaseTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DebtCaseTypeServiceImpl implements DebtCaseTypeService {
    private final DebtCaseTypeRepository debtCaseTypeRepository;

    @Override
    public List<DebtCaseType> getAllDebtCaseTypes() {
        return debtCaseTypeRepository.findAll();
    }

    @Override
    public DebtCaseType findMatchingDebtCaseType(String typeToMatch) {
        List<DebtCaseType> debtCaseTypes = debtCaseTypeRepository.findAll();
        return debtCaseTypes.stream()
                .filter(debtCaseType -> debtCaseType.getType().contains(typeToMatch))
                .findFirst()
                .orElse(null);
    }

    @Override
    public DebtCaseType getDefaultDebtCaseType() {
        List<DebtCaseType> debtCaseTypes = debtCaseTypeRepository.findAll();
        return debtCaseTypes.stream()
                .filter(debtCaseType -> debtCaseType.getId() == 10)
                .findFirst()
                .orElse(null);
    }
}
