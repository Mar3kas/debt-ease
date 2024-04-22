package com.dm.debtease.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentRequestDTO {
    @NotBlank(message = "Source id is required")
    String sourceId;

    @Positive(message = "Minimal monthly payment for each debt should be positive")
    @NotNull(message = "Minimal monthly payment for each debt should be not empty")
    BigDecimal paymentAmount;

    Boolean isPaymentInFull = true;
}
