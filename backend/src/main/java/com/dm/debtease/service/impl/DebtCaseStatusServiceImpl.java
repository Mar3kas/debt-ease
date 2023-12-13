package com.dm.debtease.service.impl;

import com.dm.debtease.model.DebtCaseStatus;
import com.dm.debtease.repository.DebtCaseStatusRepository;
import com.dm.debtease.service.DebtCaseStatusService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DebtCaseStatusServiceImpl implements DebtCaseStatusService {
    private final DebtCaseStatusRepository debtCaseStatusRepository;

    @Override
    public DebtCaseStatus getDebtCaseStatusById(int id) {
        return debtCaseStatusRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Debtcase status not found with id " + id));

    }
}
