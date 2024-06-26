package com.dm.debtease.service.impl;

import com.dm.debtease.exception.InvalidFileException;
import com.dm.debtease.exception.InvalidFileFormatException;
import com.dm.debtease.model.*;
import com.dm.debtease.model.dto.DebtorDTO;
import com.dm.debtease.service.*;
import com.dm.debtease.utils.Constants;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class CSVServiceImpl implements CSVService {
    private final DebtCaseTypeService debtCaseTypeService;
    private final DebtCaseService debtCaseService;
    private final CreditorService creditorService;
    private final DebtorService debtorService;
    private final KafkaTemplate<String, DebtCase> kafkaTemplate;
    @Value("${spring.kafka.consumer.topic-name}")
    private String topicName;

    @Override
    public void readCsvDataAndSendToKafka(MultipartFile file, String username)
            throws IOException, CsvValidationException, InvalidFileFormatException {
        if (!(file.getBytes().length == 0)) {
            if (file.getOriginalFilename() != null) {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                validateCsvFile(fileName);
                Creditor creditor = creditorService.getCreditorByUsername(username);
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
                        String typeToMatch = debtCaseTypeService.getTypeToMatch(line[4]);
                        Optional<DebtCase> existingDebtCase =
                                debtCaseService.findExistingDebtCase(username, line[5], line[8], typeToMatch, line[0],
                                        line[1]);
                        DebtCase debtCase =
                                createOrUpdateDebtCase(debtor, creditor, line, existingDebtCase, typeToMatch);
                        log.info(String.format("sending %s to kafka topic", debtCase));
                        kafkaTemplate.send(topicName, debtCase);
                    }
                }
                log.info("File read!");
                return;
            }
        }
        throw new InvalidFileException(Constants.INVALID_UPLOAD_FILE);
    }

    private void validateCsvFile(String fileName) throws InvalidFileFormatException {
        if (!fileName.toLowerCase().endsWith(".csv")) {
            log.error("File is not csv");
            throw new InvalidFileFormatException(Constants.UPLOADED_FILE_IS_NOT_CSV);
        }
    }

    private CSVReader buildCsvReader(MultipartFile file) throws IOException {
        return new CSVReaderBuilder(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build();
    }

    private DebtCase createOrUpdateDebtCase(Debtor debtor, Creditor creditor, String[] line,
                                            Optional<DebtCase> existingDebtCase, String typeToMatch) {
        DebtCase debtCase;
        if (existingDebtCase.isPresent()) {
            debtCase = existingDebtCase.get();
            debtCase.setAmountOwed(new BigDecimal(line[5]));
            debtCase.setLateInterestRate(Double.parseDouble(line[6]));
            debtCase.setDebtInterestRate(Double.parseDouble(line[7]));
            debtCase.setDueDate(line[8] != null ? LocalDateTime.parse(line[8], Constants.DATE_TIME_FORMATTER) :
                    LocalDateTime.now().plusMonths(2));
            debtCase.setModifiedDate(LocalDateTime.now());
        } else {
            debtCase = new DebtCase();
            debtCase.setCreditor(creditor);
            debtCase.setDebtor(debtor);
            debtCase.setDebtCaseStatus(DebtCaseStatus.NEW);
            debtCase.setAmountOwed(new BigDecimal(line[5]));
            debtCase.setLateInterestRate(Double.parseDouble(line[6]));
            debtCase.setDebtInterestRate(Double.parseDouble(line[7]));
            debtCase.setDueDate(line[8] != null ? LocalDateTime.parse(line[8], Constants.DATE_TIME_FORMATTER) :
                    LocalDateTime.now().plusMonths(2));
            debtCase.setCreatedDate(LocalDateTime.now());
        }
        DebtCaseType matchingDebtCaseType = debtCaseTypeService.findMatchingDebtCaseType(typeToMatch);
        debtCase.setDebtCaseType(
                matchingDebtCaseType != null ? matchingDebtCaseType : debtCaseTypeService.getDefaultDebtCaseType());
        return debtCase;
    }
}