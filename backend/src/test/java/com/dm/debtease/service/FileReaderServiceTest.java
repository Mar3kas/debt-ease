package com.dm.debtease.service;

import com.dm.debtease.service.impl.FileReaderServiceImpl;
import com.dm.debtease.utils.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class FileReaderServiceTest {
    @InjectMocks
    private FileReaderServiceImpl fileReaderService;

    @Test
    void readFileData_FilesAreExistingInTheSystem_ShouldReturnByteArrayStream() throws IOException {
        ByteArrayInputStream result = fileReaderService.readFileData(Constants.REQUEST_FORM_EXAMPLE_PATH);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.available() > 0);
    }
}
