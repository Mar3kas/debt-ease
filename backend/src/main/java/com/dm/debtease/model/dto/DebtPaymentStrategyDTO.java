package com.dm.debtease.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DebtPaymentStrategyDTO {
    @Positive(message = "Minimal monthly payment for each debt should be positive")
    @NotNull(message = "Minimal monthly payment for each debt should be not empty")
    BigDecimal minimalMonthlyPaymentForEachDebt;

    @Positive(message = "Extra monthly payment for highest debt should be positive")
    @NotNull(message = "Extra monthly payment for each debt should be not empty")
    BigDecimal extraMonthlyPaymentForHighestDebt;
}
