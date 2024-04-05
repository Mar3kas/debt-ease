package com.dm.debtease.service.impl;

import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.dto.PaymentRequestDTO;
import com.dm.debtease.service.DebtCaseService;
import com.dm.debtease.service.DebtCaseTypeService;
import com.dm.debtease.service.PaymentService;
import com.dm.debtease.utils.Constants;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class StripePaymentServiceImpl implements PaymentService {
    private final DebtCaseService debtCaseService;
    private final DebtCaseTypeService debtCaseTypeService;

    @Override
    public boolean isPaymentMade(PaymentRequestDTO paymentRequestDTO, int id) {
        DebtCase debtCase = debtCaseService.getDebtCaseById(id);
        try {
            createPaymentIntent(paymentRequestDTO, debtCase);
            debtCase = debtCaseService.updateDebtCaseAfterPayment(debtCase, paymentRequestDTO);
            if (debtCase != null) {
                return true;
            }
        } catch (StripeException e) {
            log.error("Error with Stripe payment: {}", e.getStripeError().getMessage());
            return false;
        }
        return false;
    }

    private void createPaymentIntent(PaymentRequestDTO paymentRequestDTO, DebtCase debtCase)
            throws StripeException {
        Map<String, Object> params = new HashMap<>();
        BigDecimal paymentAmount = paymentRequestDTO.getIsPaymentInFull() ?
                debtCase.getAmountOwed() :
                debtCaseService.getValidLeftAmountOwed(paymentRequestDTO.getPaymentAmount(), debtCase.getAmountOwed());
        params.put("amount", paymentAmount.multiply(BigDecimal.valueOf(Constants.STRIPE_AMOUNT_MULTIPLIER)).intValue());
        params.put("currency", "eur");
        params.put("payment_method", paymentRequestDTO.getSourceId());
        params.put("description", String.format("Payment from %s %s for %s",
                debtCase.getDebtor().getName(), debtCase.getDebtor().getSurname(),
                debtCaseTypeService.formatDebtCaseType(debtCase.getDebtCaseType().getType())));
        PaymentIntent.create(params);
    }
}