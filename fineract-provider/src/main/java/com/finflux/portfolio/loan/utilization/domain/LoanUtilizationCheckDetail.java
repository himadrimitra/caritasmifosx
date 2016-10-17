package com.finflux.portfolio.loan.utilization.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_loan_utilization_check_detail")
public class LoanUtilizationCheckDetail extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "loan_utilization_check_id", nullable = false)
    private LoanUtilizationCheck loanUtilizationCheck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id")
    private Loan loan;

    @Column(name = "loan_purpose_id", nullable = true)
    private Long loanPurposeId;

    @Column(name = "is_same_as_oroginal_purpose")
    private boolean isSameAsOriginalPurpose = false;

    @Column(name = "amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal amount;

    @Column(name = "comment", nullable = true)
    private String comment;

    protected LoanUtilizationCheckDetail() {}

    private LoanUtilizationCheckDetail(final LoanUtilizationCheck loanUtilizationCheck, final Loan loan, final Long loanPurposeId,
            final Boolean isSameAsOriginalPurpose, final BigDecimal amount, final String comment) {
        this.loanUtilizationCheck = loanUtilizationCheck;
        this.loan = loan;
        this.loanPurposeId = loanPurposeId;
        if (isSameAsOriginalPurpose != null) {
            this.isSameAsOriginalPurpose = isSameAsOriginalPurpose;
        }
        this.amount = amount;
        this.comment = comment;
    }

    public static LoanUtilizationCheckDetail create(final LoanUtilizationCheck loanUtilizationCheck, final Loan loan,
            final Long loanPurposeId, final Boolean isSameAsOriginalPurpose, final BigDecimal amount, final String comment) {
        return new LoanUtilizationCheckDetail(loanUtilizationCheck, loan, loanPurposeId, isSameAsOriginalPurpose, amount, comment);
    }
}