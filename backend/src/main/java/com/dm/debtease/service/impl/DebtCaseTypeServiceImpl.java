package com.dm.debtease.service.impl;

import com.dm.debtease.model.DebtCaseType;
import com.dm.debtease.repository.DebtCaseTypeRepository;
import com.dm.debtease.service.DebtCaseTypeService;
import com.dm.debtease.utils.Constants;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DebtCaseTypeServiceImpl implements DebtCaseTypeService {
    private final DebtCaseTypeRepository debtCaseTypeRepository;

    @Override
    public List<DebtCaseType> getAllDebtCaseTypes() {
        return debtCaseTypeRepository.findAll();
    }

    @Override
    public DebtCaseType getDebtCaseTypeById(int id) {
        Optional<DebtCaseType> optionalDebtCaseType = debtCaseTypeRepository.findById(id);
        return optionalDebtCaseType.orElseThrow(
                () -> new EntityNotFoundException(String.format(Constants.DEBT_CASE_TYPE_NOT_FOUND, id)));
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
                .filter(debtCaseType -> debtCaseType.getId() == 15)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String formatDebtCaseType(String debtTypeName) {
        String[] parts = debtTypeName.split("_");
        StringBuilder formattedName = new StringBuilder();
        for (String part : parts) {
            formattedName.append(part.charAt(0)).append(part.substring(1).toLowerCase()).append(" ");
        }
        return formattedName.toString().trim();
    }
}
