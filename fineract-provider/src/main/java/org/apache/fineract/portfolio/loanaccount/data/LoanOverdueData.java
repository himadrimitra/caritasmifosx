/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package org.apache.fineract.portfolio.loanaccount.data;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

public class LoanOverdueData {

    @SuppressWarnings("unused")
    private final BigDecimal totalOverDue;
    @SuppressWarnings("unused")
    private final BigDecimal overdueWithNextInstallment;
    
    private final LocalDate lastOverdueDate;

    public LoanOverdueData(final BigDecimal totalOverDue, final BigDecimal overdueWithNextInstallment) {
        this.totalOverDue = totalOverDue;
        this.overdueWithNextInstallment = overdueWithNextInstallment;
        this.lastOverdueDate = null;
    }
    
    public LoanOverdueData(final LocalDate lastOverdueDate){
        this.totalOverDue = null;
        this.overdueWithNextInstallment = null;
        this.lastOverdueDate = lastOverdueDate;
    }

    
    public LocalDate getLastOverdueDate() {
        return this.lastOverdueDate;
    }

}
