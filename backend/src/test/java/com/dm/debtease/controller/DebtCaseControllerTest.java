package com.dm.debtease.controller;

import com.dm.debtease.TestUtils;
import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.DebtCaseStatus;
import com.dm.debtease.model.dto.DebtCaseDTO;
import com.dm.debtease.model.dto.PaymentRequestDTO;
import com.dm.debtease.service.CSVService;
import com.dm.debtease.service.DebtCaseService;
import com.dm.debtease.service.PDFService;
import com.dm.debtease.service.PaymentService;
import com.dm.debtease.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unused")
public class DebtCaseControllerTest {
    @Mock
    private DebtCaseService debtCaseService;
    @Mock
    private CSVService csvService;
    @Mock
    private PDFService pdfService;
    @Mock
    private PaymentService paymentService;
    @InjectMocks
    private DebtCaseController debtCaseController;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(debtCaseController).build();
    }

    @Test
    void getAllDebtCases_ShouldReturnListOfDebtCases() throws Exception {
        int creditorId = 1;
        String debtorUsername = "userWithDebts";
        String debtorName = "name";
        String debtorSurname = "surname";
        String debtorEmail = "email@gmail.com";
        String debtorPhoneNumber = "+37067144213";
        String creditorUsername = "creditor123";
        String typeToMatch = "DEFAULT_DEBT";
        LocalDateTime dueDate = LocalDateTime.parse(LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER),
                Constants.DATE_TIME_FORMATTER);
        double lateInterestRate = 10.0;
        BigDecimal amountOwed = BigDecimal.valueOf(35.53);
        List<DebtCase> mockedDebtCases =
                List.of(TestUtils.setupDebtCaseTestData(creditorUsername, creditorId, debtorName, debtorSurname,
                        debtorEmail,
                        debtorPhoneNumber, typeToMatch, DebtCaseStatus.NEW, dueDate, lateInterestRate, amountOwed,
                        debtorUsername));
        when(debtCaseService.getAllDebtCases()).thenReturn(mockedDebtCases);

        MvcResult result = mockMvc.perform(get("/api/debt/cases"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].debtCaseType.type").value(typeToMatch))
                .andExpect(jsonPath("$[0].lateInterestRate").value(lateInterestRate))
                .andExpect(jsonPath("$[0].amountOwed").value(amountOwed))
                .andDo(print())
                .andReturn();

        verify(debtCaseService).getAllDebtCases();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void getDebtCaseById_ShouldReturnDebtCase() throws Exception {
        int id = 1;
        int creditorId = 1;
        String debtorUsername = "userWithDebts";
        String debtorName = "name";
        String debtorSurname = "surname";
        String debtorEmail = "email@gmail.com";
        String debtorPhoneNumber = "+37067144213";
        String creditorUsername = "creditor123";
        String typeToMatch = "DEFAULT_DEBT";
        LocalDateTime dueDate = LocalDateTime.parse(LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER),
                Constants.DATE_TIME_FORMATTER);
        double lateInterestRate = 10.0;
        BigDecimal amountOwed = BigDecimal.valueOf(35.53);
        DebtCase mockedDebtCase =
                TestUtils.setupDebtCaseTestData(creditorUsername, creditorId, debtorName, debtorSurname,
                        debtorEmail,
                        debtorPhoneNumber, typeToMatch, DebtCaseStatus.NEW, dueDate, lateInterestRate, amountOwed,
                        debtorUsername);
        when(debtCaseService.getDebtCaseById(id)).thenReturn(mockedDebtCase);

        MvcResult result = mockMvc.perform(get("/api/debt/cases/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.lateInterestRate").value(lateInterestRate))
                .andExpect(jsonPath("$.amountOwed").value(amountOwed))
                .andDo(print())
                .andReturn();

        verify(debtCaseService).getDebtCaseById(anyInt());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void getDebtCasesByCreditorUsername_ShouldReturnListOfDebtCases() throws Exception {
        int id = 1;
        int creditorId = 1;
        String debtorUsername = "userWithDebts";
        String debtorName = "name";
        String debtorSurname = "surname";
        String debtorEmail = "email@gmail.com";
        String debtorPhoneNumber = "+37067144213";
        String creditorUsername = "creditor123";
        String typeToMatch = "DEFAULT_DEBT";
        LocalDateTime dueDate = LocalDateTime.parse(LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER),
                Constants.DATE_TIME_FORMATTER);
        double lateInterestRate = 10.0;
        BigDecimal amountOwed = BigDecimal.valueOf(35.53);
        List<DebtCase> mockedDebtCase =
                List.of(TestUtils.setupDebtCaseTestData(creditorUsername, creditorId, debtorName, debtorSurname,
                        debtorEmail,
                        debtorPhoneNumber, typeToMatch, DebtCaseStatus.NEW, dueDate, lateInterestRate, amountOwed,
                        debtorUsername));
        when(debtCaseService.getDebtCasesByCreditorUsername(creditorUsername)).thenReturn(mockedDebtCase);

        MvcResult result = mockMvc.perform(get("/api/debt/cases/creditor/{username}", creditorUsername))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].creditor.user.username").value(creditorUsername))
                .andExpect(jsonPath("$[0].lateInterestRate").value(lateInterestRate))
                .andExpect(jsonPath("$[0].amountOwed").value(amountOwed))
                .andDo(print())
                .andReturn();

        verify(debtCaseService).getDebtCasesByCreditorUsername(any(String.class));
        Assertions.assertNotNull(result);
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void getDebtCasesByDebtorUsername_ShouldReturnListOfDebtCases() throws Exception {
        int id = 1;
        int creditorId = 1;
        String debtorUsername = "userWithDebts";
        String debtorName = "name";
        String debtorSurname = "surname";
        String debtorEmail = "email@gmail.com";
        String debtorPhoneNumber = "+37067144213";
        String creditorUsername = "creditor123";
        String typeToMatch = "DEFAULT_DEBT";
        LocalDateTime dueDate = LocalDateTime.parse(LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER),
                Constants.DATE_TIME_FORMATTER);
        double lateInterestRate = 10.0;
        BigDecimal amountOwed = BigDecimal.valueOf(35.53);
        List<DebtCase> mockedDebtCase =
                List.of(TestUtils.setupDebtCaseTestData(creditorUsername, creditorId, debtorName, debtorSurname,
                        debtorEmail,
                        debtorPhoneNumber, typeToMatch, DebtCaseStatus.NEW, dueDate, lateInterestRate, amountOwed,
                        debtorUsername));
        when(debtCaseService.getDebtCasesByDebtorUsername(debtorUsername)).thenReturn(mockedDebtCase);

        MvcResult result = mockMvc.perform(get("/api/debt/cases/debtor/{username}", debtorUsername))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].debtor.user.username").value(debtorUsername))
                .andExpect(jsonPath("$[0].lateInterestRate").value(lateInterestRate))
                .andExpect(jsonPath("$[0].amountOwed").value(amountOwed))
                .andDo(print())
                .andReturn();

        verify(debtCaseService).getDebtCasesByDebtorUsername(any(String.class));
        Assertions.assertNotNull(result);
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void editDebtCaseByIdAndCreditorId_ShouldReturnEditedDebtCase() throws Exception {
        BigDecimal editedAmountOwed = BigDecimal.ONE;
        int typeId = 1;
        int creditorId = 1;
        int id = 1;
        String debtorUsername = "userWithDebts";
        String debtorName = "name";
        String debtorSurname = "surname";
        String debtorEmail = "email@gmail.com";
        String debtorPhoneNumber = "+37067144213";
        String creditorUsername = "creditor123";
        String typeToMatch = "DEFAULT_DEBT";
        LocalDateTime dueDate = LocalDateTime.parse(LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER),
                Constants.DATE_TIME_FORMATTER);
        double lateInterestRate = 10.0;
        BigDecimal amountOwed = BigDecimal.valueOf(35.53);
        DebtCaseDTO mockedDebtCaseDTO = TestUtils.setupDebtCaseDTOTestData(null, typeId);
        DebtCase expectedDebtCase =
                TestUtils.setupDebtCaseTestData(creditorUsername, creditorId, debtorName, debtorSurname, debtorEmail,
                        debtorPhoneNumber, typeToMatch, DebtCaseStatus.NEW, dueDate, lateInterestRate, amountOwed,
                        debtorUsername);
        when(debtCaseService.editDebtCaseByIdAndCreditorId(any(DebtCaseDTO.class), anyInt(), anyInt())).thenReturn(
                expectedDebtCase);

        MvcResult result = mockMvc.perform(put("/api/debt/cases/{id}/creditors/{creditorId}", id, creditorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mockedDebtCaseDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.debtor.user.username").value(debtorUsername))
                .andExpect(jsonPath("$.creditor.user.username").value(creditorUsername))
                .andExpect(jsonPath("$.lateInterestRate").value(lateInterestRate))
                .andExpect(jsonPath("$.amountOwed").value(amountOwed))
                .andDo(print())
                .andReturn();

        verify(debtCaseService).editDebtCaseByIdAndCreditorId(any(DebtCaseDTO.class), anyInt(), anyInt());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void getDebtCasesReportForDebtor_ShouldReturnInputStream() throws Exception {
        String username = "testUser";
        byte[] pdfContent = "PDF_CONTENT".getBytes();
        when(pdfService.generatePdf(username)).thenReturn(new ByteArrayInputStream(pdfContent));

        MvcResult result = mockMvc.perform(get("/api/debt/cases/generate/report/debtor/{username}", username))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=debt_cases_report_" +
                        LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER_FOR_FILE) + ".pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(pdfContent))
                .andDo(print())
                .andReturn();

        verify(pdfService).generatePdf(anyString());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
    }

    @Test
    void createDebtCase_ShouldReturnSuccessfulCSVUpload() throws Exception {
        String username = "username";
        MultipartFile mockFile = new MockMultipartFile("file", "test.csv", "text/csv", "test".getBytes());
        doNothing().when(csvService).readCsvDataAndSendToKafka(any(MultipartFile.class), any(String.class));

        MvcResult result = mockMvc.perform(multipart("/api/debt/cases/creditors/{username}/file", username)
                        .file("file", mockFile.getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn();

        verify(csvService).readCsvDataAndSendToKafka(any(MultipartFile.class), anyString());
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getResponse().getContentAsString());
        Assertions.assertEquals(result.getResponse().getContentAsString(),
                "CSV uploaded successfully and debt cases are being enriched");
        Assertions.assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
    }

    @Test
    void payForDebtCaseById_ShouldReturnSuccessfullyPayed() throws Exception {
        int id = 1;
        String sourceId = "random-id";
        Boolean isPaymentInFull = true;
        PaymentRequestDTO
                paymentRequestDTO =
                TestUtils.setupPaymentRequestDTOTestData(sourceId, BigDecimal.valueOf(25.52), isPaymentInFull);
        when(paymentService.isPaymentMade(any(PaymentRequestDTO.class), anyInt())).thenReturn(true);

        MvcResult result = mockMvc.perform(post("/api/debt/cases/{id}/pay", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(paymentRequestDTO)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        verify(paymentService).isPaymentMade(any(PaymentRequestDTO.class), anyInt());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void payForDebtCaseById_ShouldReturnNotSuccessfullyPayed() throws Exception {
        int id = 1;
        String sourceId = "random-id";
        Boolean isPaymentInFull = false;
        PaymentRequestDTO
                paymentRequestDTO = TestUtils.setupPaymentRequestDTOTestData(sourceId, BigDecimal.TEN, isPaymentInFull);
        when(paymentService.isPaymentMade(any(PaymentRequestDTO.class), anyInt())).thenReturn(false);

        MvcResult result = mockMvc.perform(post("/api/debt/cases/{id}/pay", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(paymentRequestDTO)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

        verify(paymentService).isPaymentMade(any(PaymentRequestDTO.class), anyInt());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    void deleteDebtCaseByIdAndCreditorId_ShouldReturnNoContent() throws Exception {
        int id = 1;
        int creditorID = 1;
        when(debtCaseService.deleteDebtCaseByIdAndCreditorId(id, creditorID)).thenReturn(true);

        MvcResult result = mockMvc.perform(delete("/api/debt/cases/{id}/creditors/{creditorId}", id, creditorID))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn();

        verify(debtCaseService).deleteDebtCaseByIdAndCreditorId(anyInt(), anyInt());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());
    }

    @Test
    void deleteDebtCaseByInvalidIdAndCreditorId_ShouldReturnBadRequest() throws Exception {
        int id = -1;
        int creditorID = 1;
        when(debtCaseService.deleteDebtCaseByIdAndCreditorId(id, creditorID)).thenReturn(false);

        MvcResult result = mockMvc.perform(delete("/api/debt/cases/{id}/creditors/{creditorId}", id, creditorID))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

        verify(debtCaseService).deleteDebtCaseByIdAndCreditorId(anyInt(), anyInt());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }
}
