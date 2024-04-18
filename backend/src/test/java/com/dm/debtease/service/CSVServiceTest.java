package com.dm.debtease.service;

import com.dm.debtease.TestUtils;
import com.dm.debtease.exception.InvalidFileFormatException;
import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.Debtor;
import com.dm.debtease.service.impl.CSVServiceImpl;
import com.dm.debtease.utils.Constants;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unused")
public class CSVServiceTest {
    @Mock
    DebtCaseTypeService debtCaseTypeService;
    @Mock
    DebtCaseService debtCaseService;
    @Mock
    CreditorService creditorService;
    @Mock
    DebtorService debtorService;
    @Mock
    private KafkaTemplate<String, DebtCase> kafkaTemplate;
    @InjectMocks
    CSVServiceImpl csvService;
    private MultipartFile file;

    @BeforeEach
    void setup() {
        file = new MockMultipartFile("file", "test.csv", "text/csv",
                ("""
                        Name;Surname;Email;PhoneNumber;DebtType;AmountOwed;LateInterestRate;DebtInterestRate;DueDate
                        Tadas;Tadaitis;tadas@gmail.com;+37068821345;tax;84.35;5;5;2024-12-12 23:00:00
                        """).getBytes());
    }

    @Test
    void readCsvDataAndSendToKafka_WhenDebtorExistsAndDebtCaseDoesNotExist_ShouldSendToKafka()
            throws InvalidFileFormatException, CsvValidationException,
            IOException {
        String username = "username";
        String debtorName = TestUtils.INDICATOR[3];
        String debtorSurname = TestUtils.INDICATOR[4];
        when(debtorService.getDebtorByNameAndSurname(debtorName, debtorSurname)).thenReturn(new Debtor());
        when(debtCaseService.findExistingDebtCase(username, TestUtils.INDICATOR)).thenReturn(Optional.empty());
        when(debtCaseService.getTypeToMatch("tax")).thenReturn("TAX_DEBT");

        csvService.readCsvDataAndSendToKafka(file, "username");

        verify(kafkaTemplate, times(1)).send(anyString(), any(DebtCase.class));
    }

    @Test
    void readCsvDataAndSendToKafka_WhenDebtorDoesNotExistAndDebtCaseExists_ShouldSendToKafka()
            throws InvalidFileFormatException, CsvValidationException,
            IOException {
        String username = "username";
        String debtorName = TestUtils.INDICATOR[3];
        String debtorSurname = TestUtils.INDICATOR[4];
        when(debtorService.getDebtorByNameAndSurname(debtorName, debtorSurname)).thenReturn(null);
        when(debtCaseService.findExistingDebtCase(username, TestUtils.INDICATOR)).thenReturn(
                Optional.of(new DebtCase()));
        when(debtCaseService.getTypeToMatch("tax")).thenReturn("TAX_DEBT");

        csvService.readCsvDataAndSendToKafka(file, "username");

        verify(kafkaTemplate, times(1)).send(anyString(), any(DebtCase.class));
    }

    @Test
    void readCsvDataAndSendToKafka_WhenInvalidFileFormat_ShouldThrowInvalidFileFormatException() {
        MultipartFile invalidFile =
                new MockMultipartFile("file", "test.txt", "text/plain", "This is not a CSV file.".getBytes());
        String username = "user";

        InvalidFileFormatException thrown = Assertions.assertThrows(
                InvalidFileFormatException.class,
                () -> csvService.readCsvDataAndSendToKafka(invalidFile, username),
                "Expected readCsvDataAndSendToKafka to throw, but it didn't"
        );

        Assertions.assertTrue(thrown.getMessage().contains(Constants.NOT_CSV));
    }
}
