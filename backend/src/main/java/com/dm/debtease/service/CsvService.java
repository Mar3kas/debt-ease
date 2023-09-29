package com.dm.debtease.service;

import com.dm.debtease.exception.InvalidFileFormatException;
import com.dm.debtease.model.DebtCase;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CsvService {
    List<DebtCase> readCsvData(MultipartFile file, int id) throws IOException, CsvValidationException, InvalidFileFormatException;
}
