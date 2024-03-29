package com.dm.debtease.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "debtcase")
@Table(name = "debtcase")
public class DebtCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "amount_owed", nullable = false)
    BigDecimal amountOwed;

    @Column(name = "late_interest_rate", nullable = false)
    double lateInterestRate;

    @Column(name = "outstanding_balance")
    BigDecimal outstandingBalance;

    @Column(name = "due_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime dueDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id", nullable = false)
    DebtCaseType debtCaseType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id", nullable = false)
    DebtCaseStatus debtCaseStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creditor_id", nullable = false)
    Creditor creditor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "debtor_id", nullable = false)
    Debtor debtor;

    @Column(name = "created_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdDate;

    @Column(name = "modified_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime modifiedDate;
}
