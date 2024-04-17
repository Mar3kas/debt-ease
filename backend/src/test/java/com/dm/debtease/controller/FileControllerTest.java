package com.dm.debtease.controller;

import com.dm.debtease.service.FileReaderService;
import com.dm.debtease.utils.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.ByteArrayInputStream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class FileControllerTest {
    @Mock
    private FileReaderService fileReaderService;
    @InjectMocks
    private FileController fileController;
    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(fileController).build();
    }

    @Test
    public void testGetDebtCaseCSVExample_ShouldReturnInputStream() throws Exception {
        byte[] testData = "Test CSV data".getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(testData);
        when(fileReaderService.readFileData(Constants.CSV_EXAMPLE_PATH)).thenReturn(inputStream);

        MvcResult result = mockMvc.perform(get("/api/files/debt/case/example"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=debt_case_upload_example.csv"))
                .andExpect(content().contentType(MediaType.valueOf("text/csv")))
                .andExpect(content().bytes(testData))
                .andDo(print())
                .andReturn();

        verify(fileReaderService).readFileData(anyString());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
    }

    @Test
    public void testGetAgreementFormExample() throws Exception {
        byte[] testData = "Test Word document data".getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(testData);
        when(fileReaderService.readFileData(Constants.REQUEST_FORM_EXAMPLE_PATH)).thenReturn(inputStream);

        MvcResult result = mockMvc.perform(get("/api/files/agreement/form"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=agreement_form.docx"))
                .andExpect(content().contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document")))
                .andExpect(content().bytes(testData))
                .andDo(print())
                .andReturn();

        verify(fileReaderService).readFileData(anyString());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
    }
}
