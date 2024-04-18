package com.dm.debtease.controller;

import com.dm.debtease.TestUtils;
import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.DebtCaseStatus;
import com.dm.debtease.model.Payment;
import com.dm.debtease.model.dto.PaymentRequestDTO;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
public class PaymentControllerTest {
    @Mock
    private PaymentService paymentService;
    @InjectMocks
    private PaymentController paymentController;
    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
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

        MvcResult result = mockMvc.perform(post("/api/payments/{id}/pay", id)
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

        MvcResult result = mockMvc.perform(post("/api/payments/{id}/pay", id)
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
    void getPaymentsByDebtorUsername_ShouldReturnListOfPayments() throws Exception {
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
        double debtInterestRate = 10.0;
        BigDecimal amountOwed = BigDecimal.valueOf(35.53);
        DebtCase mockedDebtCase =
                TestUtils.setupDebtCaseTestData(creditorUsername, creditorId, debtorName, debtorSurname,
                        debtorEmail,
                        debtorPhoneNumber, typeToMatch, DebtCaseStatus.NEW, dueDate, lateInterestRate, debtInterestRate,
                        amountOwed, debtorUsername);
        String paymentMethod = "bank";
        String description = "you payed";
        LocalDateTime paymentDate = LocalDateTime.parse(LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER),
                Constants.DATE_TIME_FORMATTER);
        List<Payment> mockedPayments =
                List.of(TestUtils.setupPaymentTestData(mockedDebtCase, amountOwed, paymentMethod, description,
                        paymentDate));
        when(paymentService.getAllDebtorPayments(debtorUsername)).thenReturn(mockedPayments);

        MvcResult result = mockMvc.perform(get("/api/payments/{username}", debtorUsername))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].paymentMethod").value(paymentMethod))
                .andExpect(jsonPath("$[0].description").value(description))
                .andExpect(jsonPath("$[0].debtCase.debtor.user.username").value(debtorUsername))
                .andDo(print())
                .andReturn();

        verify(paymentService).getAllDebtorPayments(any(String.class));
        Assertions.assertNotNull(result);
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }
}
