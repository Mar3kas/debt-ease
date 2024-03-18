package com.dm.debtease.service.impl;

import com.dm.debtease.exception.InvalidFileFormatException;
import com.dm.debtease.model.*;
import com.dm.debtease.model.dto.DebtorDTO;
import com.dm.debtease.service.*;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class CsvServiceImpl implements CsvService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final DebtCaseTypeService debtCaseTypeService;
    private final CreditorService creditorService;
    private final DebtCaseStatusService debtCaseStatusService;
    private final DebtorService debtorService;
    private final DebtCaseService debtCaseService;
    private final KafkaTemplate<String, DebtCase> kafkaTemplate;

    @Override
    public void readCsvDataAndSendToKafka(MultipartFile file, String username) throws IOException, CsvValidationException, InvalidFileFormatException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        validateCsvFile(fileName);
        Creditor creditor = creditorService.getCreditorByUsername(username);
        DebtCaseStatus debtCaseStatus = debtCaseStatusService.getDebtCaseStatusById(1);
        log.info("Reading csv file");
        try (CSVReader reader = buildCsvReader(file)) {
            String[] line;
            reader.skip(1);
            while ((line = reader.readNext()) != null) {
                Debtor debtor = debtorService.getDebtorByNameAndSurname(line[0], line[1]);
                if (debtor == null) {
                    DebtorDTO debtorDTO = new DebtorDTO();
                    debtorDTO.setName(line[0]);
                    debtorDTO.setSurname(line[1]);
                    debtorDTO.setEmail(line[2]);
                    debtorDTO.setPhoneNumber(line[3]);
                    debtor = debtorService.createDebtor(debtorDTO);
                }
                String typeToMatch = debtCaseService.getTypeToMatch(line[4]);
                Optional<DebtCase> existingDebtCase = debtCaseService.findExistingDebtCase(username, line[5], line[7], typeToMatch, line[0], line[1]);
                DebtCase debtCase = createOrUpdateDebtCase(debtor, creditor, debtCaseStatus, line, existingDebtCase, typeToMatch);
                kafkaTemplate.send("base-debt-case-topic", debtCase);
            }
        }
        log.info("File read!");
    }

    private void validateCsvFile(String fileName) throws InvalidFileFormatException {
        if (!fileName.toLowerCase().endsWith(".csv")) {
            throw new InvalidFileFormatException("Uploaded file is not a CSV file");
        }
    }

    private CSVReader buildCsvReader(MultipartFile file) throws IOException {
        return new CSVReaderBuilder(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build();
    }

    private DebtCase createOrUpdateDebtCase(Debtor debtor, Creditor creditor, DebtCaseStatus debtCaseStatus, String[] line, Optional<DebtCase> existingDebtCase, String typeToMatch) {
        DebtCase debtCase;
        if (existingDebtCase.isPresent()) {
            debtCase = existingDebtCase.get();
            debtCase.setAmountOwed(new BigDecimal(line[5]));
            debtCase.setLateInterestRate(Double.parseDouble(line[6]));
            debtCase.setDueDate(line[7] != null ? LocalDateTime.parse(line[7], DATE_TIME_FORMATTER) : LocalDateTime.now().plusMonths(2));
        } else {
            debtCase = new DebtCase();
            debtCase.setCreditor(creditor);
            debtCase.setDebtor(debtor);
            debtCase.setDebtCaseStatus(debtCaseStatus);
            debtCase.setAmountOwed(new BigDecimal(line[5]));
            debtCase.setOutstandingBalance(BigDecimal.ZERO);
            debtCase.setLateInterestRate(Double.parseDouble(line[6]));
            debtCase.setDueDate(line[7] != null ? LocalDateTime.parse(line[7], DATE_TIME_FORMATTER) : LocalDateTime.now().plusMonths(2));
            debtCase.setIsSent(0);
        }
        DebtCaseType matchingDebtCaseType = debtCaseTypeService.findMatchingDebtCaseType(typeToMatch);
        debtCase.setDebtCaseType(matchingDebtCaseType != null ? matchingDebtCaseType : debtCaseTypeService.getDefaultDebtCaseType());
        return debtCase;
    }
}