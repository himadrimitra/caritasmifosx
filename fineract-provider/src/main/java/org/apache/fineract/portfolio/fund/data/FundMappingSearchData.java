/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.fund.data;

import java.math.BigDecimal;
import java.util.Date;

public class FundMappingSearchData {

    @SuppressWarnings("unused")
    private final Long loanId;
    @SuppressWarnings("unused")
    private final String officeName;
    @SuppressWarnings("unused")
    private final String loanProductName;
    @SuppressWarnings("unused")
    private final String districtName;
    @SuppressWarnings("unused")
    private final String stateName;
    @SuppressWarnings("unused")
    private final String fundName;
    @SuppressWarnings("unused")
    private final String loanPurposeName;

    @SuppressWarnings("unused")
    private final String loanPurposeGroup;
    @SuppressWarnings("unused")
    private final String genderName;
    @SuppressWarnings("unused")
    private final String clientTypeName;
    @SuppressWarnings("unused")
    private final String clientClassificationName;
    @SuppressWarnings("unused")
    private final BigDecimal disbursedAmount;
    @SuppressWarnings("unused")
    private final String clientName;
    @SuppressWarnings("unused")
    private final Integer loanCount;
    @SuppressWarnings("unused")
    private final BigDecimal principalOutstandingAmount;
    @SuppressWarnings("unused")
    private final Date approvedDate;
    @SuppressWarnings("unused")
    private final Date disbursementDate;
    @SuppressWarnings("unused")
    private final Integer pendingRepayment;
    @SuppressWarnings("unused")
    private final Integer paidRepayment;
    @SuppressWarnings("unused")
    private final boolean trancheDisburse;
    @SuppressWarnings("unused")
    private final Integer overDueFromDays;

    public static FundMappingSearchData instance(Long loanId, BigDecimal disbursedAmount, BigDecimal principalOutstandingAmount,
            String clientName, String clientClassificationName, String clientTypeName, String districtName, String fundName,
            String genderName, Integer loanCount, String loanProductName, String loanPurposeGroup, String loanPurposeName,
            String officeName, String stateName, final Date approvedDate, final Date disbursementDate, final Integer pendingRepayment,
            final Integer paidRepayment, boolean trancheDisburse, final Integer overDueFromDays) {
        return new FundMappingSearchData(loanId, disbursedAmount, principalOutstandingAmount, clientName, clientClassificationName,
                clientTypeName, districtName, fundName, genderName, loanCount, loanProductName, loanPurposeGroup, loanPurposeName,
                officeName, stateName, approvedDate, disbursementDate, pendingRepayment, paidRepayment, trancheDisburse, overDueFromDays);

    }

    public FundMappingSearchData(Long loanId, BigDecimal disbursedAmount, BigDecimal principalOutstandingAmount, String clientName,
            String clientClassificationName, String clientTypeName, String districtName, String fundName, String genderName,
            Integer loanCount, String loanProductName, String loanPurposeGroup, String loanPurposeName, String officeName,
            String stateName, final Date approvedDate, final Date disbursementDate, final Integer pendingRepayment,
            final Integer paidRepayment, boolean trancheDisburse, final Integer overDueFromDays) {
        this.loanId = loanId;
        this.disbursedAmount = disbursedAmount;
        this.principalOutstandingAmount = principalOutstandingAmount;
        this.clientClassificationName = clientClassificationName;
        this.clientTypeName = clientTypeName;
        this.districtName = districtName;
        this.fundName = fundName;
        this.genderName = genderName;
        this.loanCount = loanCount;
        this.loanProductName = loanProductName;
        this.loanPurposeGroup = loanPurposeGroup;
        this.loanPurposeName = loanPurposeName;
        this.officeName = officeName;
        this.stateName = stateName;
        this.clientName = clientName;
        this.approvedDate = approvedDate;
        this.disbursementDate = disbursementDate;
        this.pendingRepayment = pendingRepayment;
        this.paidRepayment = paidRepayment;
        this.trancheDisburse = trancheDisburse;
        this.overDueFromDays = overDueFromDays;
    }

}
