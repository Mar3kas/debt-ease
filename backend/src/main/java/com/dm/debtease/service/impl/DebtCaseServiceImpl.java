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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DebtCaseServiceImpl implements DebtCaseService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
                .filter(debtCase -> Objects.equals(debtCase.getDebtor().getUser().getUsername(), username))
                .toList();
    }

    @Override
    public List<DebtCase> createDebtCase(MultipartFile file, String username) throws CsvValidationException, IOException, InvalidFileFormatException {
        return csvService.readCsvData(file, username);
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
                        .orElseThrow(() -> new EntityNotFoundException("Debtcase type not found with id " + debtCaseDTO.getTypeId())));
            }
            return debtCaseRepository.save(debtCase);
        }
        throw new EntityNotFoundException("Debtcase not found with id " + id);
    }

    @Override
    public boolean deleteDebtCaseById(int id, int creditorId) {
        Optional<DebtCase> optionalDebtCase = debtCaseRepository.findByIdAndCreditor_Id(id, creditorId);
        if (optionalDebtCase.isPresent()) {
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

    @Override
    public Optional<DebtCase> findExistingDebtCase(String username, String... indicator) {
        return debtCaseRepository.findByAmountOwedAndDueDateAndDebtCaseType_TypeAndCreditor_User_UsernameAndDebtor_NameAndDebtor_Surname(
                new BigDecimal(indicator[0]),
                LocalDateTime.parse(indicator[1], DATE_TIME_FORMATTER),
                getTypeToMatch(indicator[2]),
                username,
                indicator[3],
                indicator[4]
        );
    }

    private String getTypeToMatch(String type) {
        return type.toUpperCase().contains("_DEBT") ? type.toUpperCase() : type.toUpperCase().concat("_DEBT");
    }
}
