package com.dm.debtease.service.impl;

import com.dm.debtease.exception.InvalidFileFormatException;
import com.dm.debtease.model.Creditor;
import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.DebtCaseStatus;
import com.dm.debtease.model.DebtCaseType;
import com.dm.debtease.model.Debtor;
import com.dm.debtease.repository.DebtCaseStatusRepository;
import com.dm.debtease.repository.DebtCaseTypeRepository;
import com.dm.debtease.repository.DebtorRepository;
import com.dm.debtease.service.CreditorService;
import com.dm.debtease.service.CsvService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Log4j2
public class CsvServiceImpl implements CsvService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final DebtCaseTypeRepository debtCaseTypeRepository;
    private final DebtorRepository debtorRepository;
    private final DebtCaseStatusRepository debtCaseStatusRepository;
    private final CreditorService creditorService;

    @Autowired
    public CsvServiceImpl(DebtCaseTypeRepository debtCaseTypeRepository, DebtorRepository debtorRepository,
                          DebtCaseStatusRepository debtCaseStatusRepository,
                          CreditorService creditorService) {
        this.debtCaseTypeRepository = debtCaseTypeRepository;
        this.debtorRepository = debtorRepository;
        this.debtCaseStatusRepository = debtCaseStatusRepository;
        this.creditorService = creditorService;
    }

    @Override
    public List<DebtCase> readCsvData(MultipartFile file, int id) throws IOException, CsvValidationException, InvalidFileFormatException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (!fileName.toLowerCase().endsWith(".csv")) {
            throw new InvalidFileFormatException("Uploaded file is not a CSV file");
        }
        List<DebtCase> debtCases = new ArrayList<>();
        List<DebtCaseType> debtCaseTypes = debtCaseTypeRepository.findAll();
        Creditor creditor = creditorService.getCreditorById(id);
        DebtCaseStatus debtCaseStatus = debtCaseStatusRepository.findById(1).orElseThrow(() -> new EntityNotFoundException("Debtcase status not found with id 1"));
        log.info("Reading csv file");
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] line;
            reader.skip(1);
            while ((line = reader.readNext()) != null) {
                Optional<Debtor> existingDebtor = debtorRepository.findByNameAndSurname(line[0], line[1]);
                Debtor debtor;
                debtor = existingDebtor.orElseGet(Debtor::new);
                debtor.setName(line[0]);
                debtor.setSurname(line[1]);
                debtor.setEmail(line[2]);
                debtor.setPhoneNumber(line[3]);

                String typeToMatch = line[4].toUpperCase();
                DebtCase debtCase = new DebtCase();
                debtCase.setCreditor(creditor);

                DebtCaseType matchingDebtCaseType = debtCaseTypes.stream()
                        .filter(debtCaseType -> debtCaseType.getType().contains(typeToMatch))
                        .findFirst()
                        .orElse(null);

                debtCase.setDebtCaseType(matchingDebtCaseType != null ? matchingDebtCaseType : debtCaseTypes.stream()
                        .filter(debtCaseType -> debtCaseType.getId() == 10)
                        .findFirst()
                        .orElse(null));

                debtCase.setDebtCaseStatus(debtCaseStatus);
                debtCase.setAmountOwed(new BigDecimal(line[5]));
                debtCase.setDueDate(line[6] != null ? LocalDateTime.parse(line[6], DATE_TIME_FORMATTER) : LocalDateTime.now().plusMonths(2));
                debtCase.setIsSent(0);

                debtor.setDebtCase(debtCase);
                debtorRepository.save(debtor);

                debtCases.add(debtCase);
            }
        }
        log.info("File read!");

        return debtCases;
    }
}