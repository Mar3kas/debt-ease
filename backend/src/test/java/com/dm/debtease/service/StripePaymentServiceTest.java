package com.dm.debtease.service;

import com.dm.debtease.model.Payment;
import com.dm.debtease.repository.PaymentRepository;
import com.dm.debtease.service.impl.StripePaymentServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unused")
public class StripePaymentServiceTest {
    @Mock
    private DebtCaseService debtCaseService;
    @Mock
    private DebtCaseTypeService debtCaseTypeService;
    @Mock
    private PaymentRepository paymentRepository;
    @InjectMocks
    private StripePaymentServiceImpl stripePaymentService;

    @Test
    void getAllDebtorPayments_WhenDebtCasesExist_ShouldReturnNonEmptyList() {
        String username = "username";
        when(paymentRepository.findByDebtCase_Debtor_User_Username(anyString())).thenReturn(List.of(new Payment()));

        List<Payment> actualPayments = stripePaymentService.getAllDebtorPayments(username);

        Assertions.assertNotNull(actualPayments);
        Assertions.assertFalse(actualPayments.isEmpty());
    }
}
