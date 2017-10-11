/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.fund.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_fund_mapping_history")
public class FundMappingHistory extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @ManyToOne
    @JoinColumn(name = "fund_id", nullable = false)
    private Fund fund;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private AppUser createdBy;

    @Column(name = "assignment_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date assignmentDate;

    @Column(name = "assignment_end_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date assignmentEndDate;

    public FundMappingHistory(final Loan loan, final Fund fund, final AppUser createdBy, final Date assignmentDate,
            final Date assignmentEndDate) {
        this.loan = loan;
        this.fund = fund;
        this.createdBy = createdBy;
        this.assignmentDate = assignmentDate;
        this.assignmentEndDate = assignmentEndDate;
    }

    public FundMappingHistory() {
        super();
    }

    public static FundMappingHistory instance(final Loan loan, final Fund fund, final AppUser createdBy, final Date assignmentDate,
            final Date assignmentEndDate) {
        return new FundMappingHistory(loan, fund, createdBy, assignmentDate, assignmentEndDate);
    }

    public Date getAssignmentEndDate() {
        return this.assignmentEndDate;
    }

    public void setAssignmentEndDate(Date assignmentEndDate) {
        this.assignmentEndDate = assignmentEndDate;
    }

}
