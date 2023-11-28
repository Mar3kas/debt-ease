package com.dm.debtease.service;

import com.dm.debtease.exception.InvalidFileFormatException;
import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.dto.DebtCaseDTO;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DebtCaseService {
    List<DebtCase> getAllDebtCases();

    DebtCase getDebtCaseById(int id);

    List<DebtCase> getDebtCasesByCreditorUsername(String username);

    List<DebtCase> getDebtCasesByDebtorUsername(String username);

    List<DebtCase> createDebtCase(MultipartFile file, int id) throws CsvValidationException, IOException, InvalidFileFormatException;

    DebtCase editDebtCaseById(DebtCaseDTO debtCaseDTO, int id, int creditorId);

    boolean deleteDebtCaseById(int creditorId, int id);

    void markDebtCaseEmailAsSentById(int id);
}
