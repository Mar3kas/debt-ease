package com.dm.debtease.service;

import com.dm.debtease.model.Payment;
import com.dm.debtease.model.dto.PaymentRequestDTO;

import java.util.List;

public interface PaymentService {
    boolean isPaymentMade(PaymentRequestDTO paymentRequestDTO, int id);

    List<Payment> getAllDebtorPayments(String username);
}
