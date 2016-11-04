/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package org.apache.fineract.portfolio.charge.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_charge_slab")
public class ChargeSlab extends AbstractPersistable<Long> {

    @Column(name = "from_loan_amount", scale = 6, precision = 19)
    private BigDecimal fromLoanAmount;

    @Column(name = "to_loan_amount", scale = 6, precision = 19)
    private BigDecimal toLoanAmount;

    @Column(name = "amount", scale = 6, precision = 19)
    private BigDecimal amount;

    public ChargeSlab() {

    }

    public ChargeSlab(final BigDecimal fromLoanAmount, final BigDecimal toLoanAmount, BigDecimal amount) {
        this.fromLoanAmount = fromLoanAmount;
        this.toLoanAmount = toLoanAmount;
        this.amount = amount;
    }

    public static ChargeSlab createNew(final BigDecimal fromLoanAmount, final BigDecimal toLoanAmount, BigDecimal amount) {
        return new ChargeSlab(fromLoanAmount, toLoanAmount, amount);
    }

    public boolean isLoanAmountFallsInSlab(final BigDecimal principalAmount) {
        boolean isLoanAmountFallsInSlab = false;
        if (MathUtility.isEqualOrGreater(principalAmount, this.fromLoanAmount) && MathUtility.isLesserOrEqualTo(principalAmount, this.toLoanAmount)) {
            isLoanAmountFallsInSlab = true;
        }
        return isLoanAmountFallsInSlab;
    }
	
}
