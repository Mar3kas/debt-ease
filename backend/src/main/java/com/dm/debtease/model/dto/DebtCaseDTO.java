package com.dm.debtease.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class DebtCaseDTO {
    @Positive(message = "Amount Owed must be positive")
    @NotNull(message = "Amount Owed cannot be null")
    BigDecimal amountOwed;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime dueDate;
    int typeId;
}
