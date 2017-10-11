package com.finflux.loanapplicationreference.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_loan_app_sanction_tranche")
public class LoanApplicationSanctionTranche extends AbstractPersistable<Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_app_sanction_id", nullable = false)
    private LoanApplicationSanction loanApplicationSanction;

    @Column(name = "tranche_amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal trancheAmount;

    @Column(name = "fixed_emi_amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal fixedEmiAmount;

    @Temporal(TemporalType.DATE)
    @Column(name = "expected_tranche_disbursement_date", nullable = false)
    private Date expectedTrancheDisbursementDate;

    protected LoanApplicationSanctionTranche() {}

    LoanApplicationSanctionTranche(final LoanApplicationSanction loanApplicationSanction, final BigDecimal trancheAmount,
            final BigDecimal fixedEmiAmount, final Date expectedTrancheDisbursementDate) {
        this.loanApplicationSanction = loanApplicationSanction;
        this.trancheAmount = trancheAmount;
        this.fixedEmiAmount = fixedEmiAmount;
        this.expectedTrancheDisbursementDate = expectedTrancheDisbursementDate;
    }

    public static LoanApplicationSanctionTranche create(final LoanApplicationSanction loanApplicationSanction,
            final BigDecimal trancheAmount, final BigDecimal fixedEmiAmount, final Date expectedTrancheDisbursementDate) {
        return new LoanApplicationSanctionTranche(loanApplicationSanction, trancheAmount, fixedEmiAmount, expectedTrancheDisbursementDate);
    }
}
