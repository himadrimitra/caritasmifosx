/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.fund.data;

import java.math.BigDecimal;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;

public class FundLoanPurposeData {

    @SuppressWarnings("unused")
    private final Long id;
    @SuppressWarnings("unused")
    private final Long fundId;
    @SuppressWarnings("unused")
    private final CodeValueData loanPurpose;
    @SuppressWarnings("unused")
    private BigDecimal loanPurposeAmount;
    private BigDecimal totalAmount;

    public FundLoanPurposeData(Long id, Long fundId, CodeValueData loanPurpose, BigDecimal loanPurposeAmount,
            BigDecimal totalAmount) {
        this.id = id;
        this.fundId = fundId;
        this.loanPurpose = loanPurpose;
        this.loanPurposeAmount = loanPurposeAmount;
        this.totalAmount = totalAmount;
    }

    public static FundLoanPurposeData instance(Long id, Long fundId, CodeValueData loanPurpose, BigDecimal loanPurposeAmount, BigDecimal totalAmount) {
        return new FundLoanPurposeData(id, fundId, loanPurpose, loanPurposeAmount, totalAmount);
    }

}
