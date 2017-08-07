/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.portfolio.loanproduct.creditbureau.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_creditbureau_loanproduct_office_mapping", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "loan_product_id", "office_id" }, name = "uk_loanproduct_office_mapping") })
public class CreditBureauLoanProductOfficeMapping extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "credit_bureau_loan_product_mapping_id", nullable = false)
    private CreditBureauLoanProductMapping creditBureauLoanProductMapping;

    @Column(name = "loan_product_id", nullable = true)
    private Long loanProductId;

    @Column(name = "office_id", nullable = true)
    private Long officeId;

    protected CreditBureauLoanProductOfficeMapping() {}

    private CreditBureauLoanProductOfficeMapping(final CreditBureauLoanProductMapping creditBureauLoanProductMapping,
            final Long loanProductId, final Long officeId) {
        this.creditBureauLoanProductMapping = creditBureauLoanProductMapping;
        this.loanProductId = loanProductId;
        this.officeId = officeId;
    }

    public static CreditBureauLoanProductOfficeMapping create(final CreditBureauLoanProductMapping creditBureauLoanProductMapping,
            final Long loanProductId, final Long officeId) {
        return new CreditBureauLoanProductOfficeMapping(creditBureauLoanProductMapping, loanProductId, officeId);
    }

    public CreditBureauLoanProductMapping getCreditBureauLoanProductMapping() {
        return this.creditBureauLoanProductMapping;
    }

    public Long getLoanProductId() {
        return this.loanProductId;
    }

    public Long getOfficeId() {
        return this.officeId;
    }

}