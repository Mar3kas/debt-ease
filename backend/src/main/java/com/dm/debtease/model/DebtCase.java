package com.dm.debtease.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "debt_case")
@Table(name = "debt_case", schema = "public")
public class DebtCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "amount_owed", nullable = false)
    BigDecimal amountOwed;

    @Column(name = "late_interest_rate", nullable = false)
    double lateInterestRate;

    @Column(name = "debt_interest_rate", nullable = false)
    double debtInterestRate;

    @Column(name = "due_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime dueDate;

    @ManyToOne()
    @JoinColumn(name = "type_id", nullable = false)
    DebtCaseType debtCaseType;

    @Enumerated(EnumType.STRING)
    @Column(name = "case_status")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    DebtCaseStatus debtCaseStatus;

    @ManyToOne()
    @JoinColumn(name = "creditor_id", nullable = false)
    Creditor creditor;

    @ManyToOne()
    @JoinColumn(name = "debtor_id", nullable = false)
    Debtor debtor;

    @Column(name = "created_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdDate;

    @Column(name = "modified_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime modifiedDate;

    public DebtCase(DebtCase original) {
        this.id = original.getId();
        this.amountOwed = original.getAmountOwed();
        this.lateInterestRate = original.getLateInterestRate();
        this.debtInterestRate = original.getDebtInterestRate();
        this.dueDate = original.getDueDate();
        this.debtCaseType = original.getDebtCaseType();
        this.debtCaseStatus = original.getDebtCaseStatus();
        this.creditor = original.getCreditor();
        this.debtor = original.getDebtor();
        this.createdDate = original.getCreatedDate();
        this.modifiedDate = original.getModifiedDate();
    }
}
