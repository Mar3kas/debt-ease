package com.dm.debtease.service.impl;

import com.dm.debtease.service.FileReaderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@SuppressWarnings("unused")
@Log4j2
public class FileReaderServiceImpl implements FileReaderService {
    @Override
    public ByteArrayInputStream readFileData(String filePath) throws IOException {
        try (InputStream inputStream = new FileInputStream(filePath)) {
            return new ByteArrayInputStream(inputStream.readAllBytes());
        }
    }
}
