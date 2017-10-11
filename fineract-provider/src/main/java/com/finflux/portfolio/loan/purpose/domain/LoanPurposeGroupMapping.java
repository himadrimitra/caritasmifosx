package com.finflux.portfolio.loan.purpose.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_loan_purpose_group_mapping")
public class LoanPurposeGroupMapping extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "loan_purpose_group_id", referencedColumnName = "id", nullable = false)
    private LoanPurposeGroup loanPurposeGroup;

    @Column(name = "is_active", length = 1, nullable = false)
    private Boolean isActive;

    protected LoanPurposeGroupMapping() {}

    private LoanPurposeGroupMapping(final LoanPurposeGroup loanPurposeGroup, final Boolean isActive) {
        this.loanPurposeGroup = loanPurposeGroup;
        this.isActive = isActive;
    }

    public static LoanPurposeGroupMapping create(final LoanPurposeGroup loanPurposeGroup, final Boolean isActive) {
        return new LoanPurposeGroupMapping(loanPurposeGroup, isActive);
    }

    public LoanPurposeGroup getLoanPurposeGroup() {
        return this.loanPurposeGroup;
    }

}