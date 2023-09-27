package com.dm.debtease.service;

import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.dto.DebtCaseDTO;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DebtCaseService {
    List<DebtCase> getAllDebtCases();
    DebtCase getDebtCaseById(int id);
    List<DebtCase> getDebtCasesByDebtorId(int id);
    List<DebtCase> getDebtCasesByCreditorId(int id);
    List<DebtCase> createDebtCase(MultipartFile file, int id) throws CsvValidationException, IOException;
    DebtCase editDebtCaseById(DebtCaseDTO debtCaseDTO, int id, int debtorId, int typeId);
    boolean deleteDebtCaseById(int id);
    void markDebtCaseEmailAsSentById(int id);
}
