package com.dm.debtease.service.impl;

import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.dto.DebtCaseDTO;
import com.dm.debtease.repository.DebtCaseRepository;
import com.dm.debtease.repository.DebtCaseTypeRepository;
import com.dm.debtease.service.CsvService;
import com.dm.debtease.service.DebtCaseService;
import com.dm.debtease.service.DebtorService;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DebtCaseServiceImpl implements DebtCaseService {
    private final DebtCaseRepository debtCaseRepository;
    private final DebtCaseTypeRepository debtCaseTypeRepository;
    private final CsvService csvService;
    private final DebtorService debtorService;
    @Autowired
    public DebtCaseServiceImpl(DebtCaseRepository debtCaseRepository, DebtCaseTypeRepository debtCaseTypeRepository,
                               CsvService csvService, DebtorService debtorService) {
        this.debtCaseRepository = debtCaseRepository;
        this.debtCaseTypeRepository = debtCaseTypeRepository;
        this.csvService = csvService;
        this.debtorService = debtorService;
    }
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
    public List<DebtCase> getDebtCasesByDebtorId(int id) {
        List<DebtCase> debtCases = debtCaseRepository.findAll();

        return debtCases.stream().filter(debtCase -> debtCase.getDebtor().getId() == id).toList();
    }
    @Override
    public List<DebtCase> getDebtCasesByCreditorId(int id) {
        List<DebtCase> debtCases = debtCaseRepository.findAll();

        return debtCases.stream().filter(debtCase -> debtCase.getCreditor().getId() == id).toList();
    }
    @Override
    public List<DebtCase> createDebtCase(MultipartFile file, int id) throws CsvValidationException, IOException {
        List<DebtCase> debtCases = csvService.readCsvData(file, id);

        return debtCaseRepository.saveAll(debtCases);
    }
    @Override
    public DebtCase editDebtCaseById(DebtCaseDTO debtCaseDTO, int id, int debtorId, int typeId) {
        Optional<DebtCase> optionalDebtCase = debtCaseRepository.findById(id);
        if (optionalDebtCase.isPresent()) {
            DebtCase debtCase = optionalDebtCase.get();
            if (Objects.nonNull(debtCaseDTO.getAmountOwed())) {
                debtCase.setAmountOwed(debtCaseDTO.getAmountOwed());
            }
            if (Objects.nonNull(debtCaseDTO.getDueDate())) {
                debtCase.setDueDate(debtCaseDTO.getDueDate());
            }
            if (Objects.nonNull(debtCaseDTO.getDebtorDTO())) {
                debtCase.setDebtor(debtorService.editDebtorById(debtCaseDTO.getDebtorDTO(), debtorId));
            }
            if (typeId > 0) {
                debtCase.setDebtCaseType(debtCaseTypeRepository.findById(typeId)
                        .orElseThrow(() -> new EntityNotFoundException("Debtcase type not found with id " + typeId)));
            }

            return debtCaseRepository.save(debtCase);
        }

        throw new EntityNotFoundException("Debtcase not found with id " + id);
    }
    @Override
    public boolean deleteDebtCaseById(int id) {
        Optional<DebtCase> optionalDebtCase = debtCaseRepository.findById(id);
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
}
