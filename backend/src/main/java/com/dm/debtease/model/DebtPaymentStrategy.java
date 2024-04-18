package com.dm.debtease.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class DebtPaymentStrategy {
    @JsonProperty("snowballBalanceEachMonth")
    List<BigDecimal> snowballBalanceEachMonth;

    @JsonProperty("avalancheBalanceEachMonth")
    List<BigDecimal> avalancheBalanceEachMonth;
}
