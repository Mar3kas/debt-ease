package com.dm.debtease.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentRequestDTO {
    @NotBlank(message = "Source id is required")
    String sourceId;

    BigDecimal paymentAmount;

    Boolean isPaymentInFull;
}
