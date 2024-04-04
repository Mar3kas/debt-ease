package com.dm.debtease.service;

import com.dm.debtease.model.dto.PaymentRequestDTO;

public interface PaymentService {
    boolean isPaymentMade(PaymentRequestDTO paymentRequestDTO, int id);
}
