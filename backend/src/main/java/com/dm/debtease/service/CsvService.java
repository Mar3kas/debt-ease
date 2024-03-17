package com.dm.debtease.service;

import com.dm.debtease.exception.InvalidFileFormatException;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CsvService {
    void readCsvDataAndSendToKafka(MultipartFile file, String username) throws IOException, CsvValidationException, InvalidFileFormatException;
}
