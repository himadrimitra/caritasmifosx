/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.fund.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_fund_loan_purpose")
public class FundLoanPurpose extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "fund_id", nullable = false)
    private Fund fund;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_purpose_id", nullable = false)
    private CodeValue loanPurpose;

    @Column(name = "loan_purpose_amount", nullable = false)
    private BigDecimal loanPurposeAmount;

    private FundLoanPurpose(final Fund fund, final CodeValue loanPurpose, final BigDecimal loanPurposeAmount) {
        this.loanPurpose = loanPurpose;
        this.loanPurposeAmount = loanPurposeAmount;
        this.fund = fund;
    }

    public FundLoanPurpose() {
        super();
    }

    public static FundLoanPurpose instanceWithoutFund(final CodeValue loanPurpose, final BigDecimal loanPurposeAmount) {
        final Fund fund = null;
        return new FundLoanPurpose(fund, loanPurpose, loanPurposeAmount);
    }

    public Fund getFund() {
        return this.fund;
    }

    public void setFund(Fund fund) {
        this.fund = fund;
    }

}
