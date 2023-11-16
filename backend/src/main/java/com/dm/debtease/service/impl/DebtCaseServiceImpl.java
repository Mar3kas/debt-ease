package com.dm.debtease.service.impl;

import com.dm.debtease.exception.InvalidFileFormatException;
import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.dto.DebtCaseDTO;
import com.dm.debtease.repository.DebtCaseRepository;
import com.dm.debtease.repository.DebtCaseTypeRepository;
import com.dm.debtease.service.CsvService;
import com.dm.debtease.service.DebtCaseService;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DebtCaseServiceImpl implements DebtCaseService {
    private final DebtCaseRepository debtCaseRepository;
    private final DebtCaseTypeRepository debtCaseTypeRepository;
    private final CsvService csvService;

    @Override
    public List<DebtCase> getAllDebtCases() {
        return debtCaseRepository.findAll();
    }

    @Override
    public DebtCase getDebtCaseById(int id) {
        Optional<DebtCase> optionalDebtCase = debtCaseRepository.findById(id);

        return optionalDebtCase.orElseThrow(() -> new EntityNotFoundException("Debtcase not found with id " + id));
    }

    @Override
    public List<DebtCase> getDebtCasesByCreditorUsername(String username) {
        List<DebtCase> debtCases = debtCaseRepository.findAll();

        return debtCases.stream()
                .filter(debtCase ->
                        Objects.equals(debtCase.getCreditor().getUser().getUsername(), username))
                .toList();
    }

    @Override
    public List<DebtCase> getDebtCasesByDebtorUsername(String username) {
        List<DebtCase> debtCases = debtCaseRepository.findAll();

        return debtCases.stream()
                .filter(debtCase -> debtCase.getDebtors().stream()
                        .anyMatch(debtor -> Objects.equals(debtor.getUser().getUsername(), username)))
                .toList();
    }

    @Override
    public List<DebtCase> createDebtCase(MultipartFile file, int id) throws CsvValidationException, IOException, InvalidFileFormatException {
        return csvService.readCsvData(file, id);
    }

    @Override
    public DebtCase editDebtCaseById(DebtCaseDTO debtCaseDTO, int id, int creditorId) {
        Optional<DebtCase> optionalDebtCase = debtCaseRepository.findById(id);
        if (optionalDebtCase.isPresent()) {
            DebtCase debtCase = optionalDebtCase.get();
            if (debtCase.getCreditor().getId() == creditorId) {
                if (Objects.nonNull(debtCaseDTO.getAmountOwed())) {
                    debtCase.setAmountOwed(debtCaseDTO.getAmountOwed());
                }
                if (Objects.nonNull(debtCaseDTO.getDueDate())) {
                    debtCase.setDueDate(debtCaseDTO.getDueDate());
                }
                if (debtCaseDTO.getTypeId() > 0) {
                    debtCase.setDebtCaseType(debtCaseTypeRepository.findById(debtCaseDTO.getTypeId())
                            .orElseThrow(() -> new EntityNotFoundException("Debtcase type not found with id " + debtCaseDTO.getTypeId())));
                }

                return debtCaseRepository.save(debtCase);
            }
        }

        throw new EntityNotFoundException("Debtcase not found with id " + id);
    }

    @Override
    public boolean deleteDebtCaseById(int id, int creditorId) {
        Optional<DebtCase> optionalDebtCase = debtCaseRepository.findById(id);
        if (optionalDebtCase.isPresent() && optionalDebtCase.get().getCreditor().getId() == creditorId) {
            debtCaseRepository.deleteById(id);

            return true;
        }

        throw new EntityNotFoundException("Debtcase not found with id " + id);
    }

    @Override
    public void markDebtCaseEmailAsSentById(int id) {
        DebtCase debtCase = debtCaseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Debtcase not found with id " + id));

        debtCase.setIsSent(1);
        debtCaseRepository.save(debtCase);
    }
}
