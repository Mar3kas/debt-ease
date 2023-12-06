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
}
