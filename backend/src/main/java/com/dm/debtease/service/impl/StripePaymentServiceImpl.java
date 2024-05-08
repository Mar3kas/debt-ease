package com.dm.debtease.service.impl;

import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.Payment;
import com.dm.debtease.model.dto.PaymentRequestDTO;
import com.dm.debtease.repository.PaymentRepository;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class StripePaymentServiceImpl implements PaymentService {
    private final DebtCaseService debtCaseService;
    private final DebtCaseTypeService debtCaseTypeService;
    private final PaymentRepository paymentRepository;

    @Override
    public boolean isPaymentMade(PaymentRequestDTO paymentRequestDTO, int id) {
        DebtCase debtCase = debtCaseService.getDebtCaseById(id);
        try {
            PaymentIntent paymentIntent = createPaymentIntent(paymentRequestDTO, debtCase);
            debtCase = debtCaseService.updateDebtCaseAfterPayment(debtCase, paymentRequestDTO);
            savePayment(debtCase, paymentIntent);
            return true;
        } catch (StripeException e) {
            log.error("Error with Stripe payment: {}", e.getStripeError().getMessage());
            return false;
        }
    }

    @Override
    public List<Payment> getAllDebtorPayments(String username) {
        return paymentRepository.findByDebtCase_Debtor_User_Username(username);
    }

    private void savePayment(DebtCase debtCase, PaymentIntent paymentIntent) {
        Payment payment = new Payment();
        payment.setPaymentDate(LocalDateTime.now());
        payment.setDebtCase(debtCase);
        payment.setAmount(BigDecimal.valueOf(paymentIntent.getAmount()).divide(BigDecimal.valueOf(Constants.STRIPE_AMOUNT_MULTIPLIER)));
        payment.setDescription(paymentIntent.getDescription());
        payment.setPaymentMethod(paymentIntent.getPaymentMethod());
        paymentRepository.save(payment);
    }

    private PaymentIntent createPaymentIntent(PaymentRequestDTO paymentRequestDTO, DebtCase debtCase)
            throws StripeException {
        Map<String, Object> params = new HashMap<>();
        BigDecimal paymentAmount = debtCase.getAmountOwed();
        if (!paymentRequestDTO.getIsPaymentInFull()) {
            BigDecimal amountLeft = debtCaseService.getValidLeftAmountOwed(
                    paymentRequestDTO.getPaymentAmount(), paymentAmount);
            paymentAmount = paymentAmount.subtract(amountLeft);
            if (paymentAmount.compareTo(BigDecimal.ZERO) == 0) {
                paymentAmount = debtCase.getAmountOwed();
            }
        }
        params.put("amount", paymentAmount.multiply(BigDecimal.valueOf(Constants.STRIPE_AMOUNT_MULTIPLIER)).intValue());
        params.put("currency", "eur");
        params.put("payment_method", paymentRequestDTO.getSourceId());
        params.put("description", String.format("Payment from %s %s for %s",
                debtCase.getDebtor().getName(), debtCase.getDebtor().getSurname(),
                debtCaseTypeService.formatDebtCaseType(debtCase.getDebtCaseType().getType())));
        return PaymentIntent.create(params);
    }
}