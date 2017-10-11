/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.data;

import java.math.BigDecimal;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;

@SuppressWarnings("unused")
public class GroupLoanIndividualMonitoringData {

    private final Long id;
    private final Long loanId;
    private final BigDecimal totalLoanAmount;
    private final Long clientId;
    private final String clientName;
    private final String clientExternalID;
    private BigDecimal proposedAmount;
    private BigDecimal approvedAmount;
    private BigDecimal disbursedAmount;
    private CodeValueData loanPurpose;
    private Boolean isClientSelected;
    private BigDecimal chargeAmount;
    private BigDecimal adjustedAmount;
    private BigDecimal installmentAmount;
    private BigDecimal totalPaybleAmount;
    private BigDecimal paidInterestAmount;
    private BigDecimal paidAmount;
    private BigDecimal interestAmount;
    private BigDecimal paidPrincipalAmount;
    private BigDecimal paidChargeAmount;
    private BigDecimal waivedInterestAmount;
    private BigDecimal waivedChargeAmount;
    private BigDecimal principalWrittenOffAmount;
    private BigDecimal interestWrittenOffAmount;
    private BigDecimal chargeWrittenOffAmount;
    private BigDecimal penaltyAmount;
    private BigDecimal paidPenaltyAmount;
    private BigDecimal waivedPenaltyAmount;
    private BigDecimal penaltywrittenOffAmount;
    private BigDecimal transactionAmount;
    private BigDecimal remainingTransactionAmount;
    private Boolean isActive;
    private BigDecimal totalPrincipalOutstanding;
    private BigDecimal totalInterestOutstanding;
    private BigDecimal totalFeeChargeOutstanding;
    private BigDecimal totalpenaltyChargeOutstanding;
    private BigDecimal totalLoanOutstandingAmount;

    public GroupLoanIndividualMonitoringData(final Long id, final Long loanId, final BigDecimal totalLoanAmount, final Long clientId,
            final String clientName, final String clientExternalID, final BigDecimal proposedAmount, final BigDecimal approvedAmount,
            final BigDecimal disbursedAmount, final CodeValueData loanPurpose, final Boolean isClientSelected,
            final BigDecimal chargeAmount, final BigDecimal adjustedAmount, final BigDecimal installmentAmount,
            final BigDecimal totalPaybleAmount, final BigDecimal paidInterestAmount, final BigDecimal paidAmount,
            final BigDecimal interestAmount, final BigDecimal paidPrincipalAmount, final BigDecimal paidChargeAmount,
            final BigDecimal waivedInterestAmount, final BigDecimal waivedChargeAmount, BigDecimal principalWrittenOffAmount,
            BigDecimal interestWrittenOffAmount, BigDecimal chargeWrittenOffAmount, BigDecimal penaltyAmount, BigDecimal paidPenaltyAmount,
            BigDecimal waivedPenaltyAmount, BigDecimal penaltywrittenOffAmount, final BigDecimal remainingTransactionAmount,
            BigDecimal transactionAmount, Boolean isActive, BigDecimal totalPrincipalOutstanding, BigDecimal totalInterestOutstanding,
            BigDecimal totalFeeChargeOutstanding, BigDecimal totalpenaltyChargeOutstanding, BigDecimal totalLoanOutstandingAmount) {
        this.id = id;
        this.loanId = loanId;
        this.totalLoanAmount = totalLoanAmount;
        this.clientId = clientId;
        this.clientName = clientName;
        this.proposedAmount = proposedAmount;
        this.approvedAmount = approvedAmount;
        this.disbursedAmount = disbursedAmount;
        this.loanPurpose = loanPurpose;
        this.clientExternalID = clientExternalID;
        this.isClientSelected = isClientSelected;
        this.chargeAmount = chargeAmount;
        this.adjustedAmount = adjustedAmount;
        this.installmentAmount = installmentAmount;
        this.totalPaybleAmount = totalPaybleAmount;
        this.paidInterestAmount = paidInterestAmount;
        this.paidAmount = paidAmount;
        this.interestAmount = interestAmount;
        this.paidPrincipalAmount = paidPrincipalAmount;
        this.paidChargeAmount = paidChargeAmount;
        this.waivedInterestAmount = waivedInterestAmount;
        this.waivedChargeAmount = waivedChargeAmount;
        this.principalWrittenOffAmount = principalWrittenOffAmount;
        this.interestWrittenOffAmount = interestWrittenOffAmount;
        this.chargeWrittenOffAmount = chargeWrittenOffAmount;
        this.penaltyAmount = penaltyAmount;
        this.paidPenaltyAmount = paidPenaltyAmount;
        this.waivedPenaltyAmount = waivedPenaltyAmount;
        this.penaltywrittenOffAmount = penaltywrittenOffAmount;
        this.remainingTransactionAmount = remainingTransactionAmount;
        this.transactionAmount = transactionAmount;
        this.isActive = isActive;
        this.totalPrincipalOutstanding = totalPrincipalOutstanding;
        this.totalInterestOutstanding = totalInterestOutstanding;
        this.totalFeeChargeOutstanding = totalFeeChargeOutstanding;
        this.totalpenaltyChargeOutstanding = totalpenaltyChargeOutstanding;
        this.totalLoanOutstandingAmount = totalLoanOutstandingAmount;
    }

    public static GroupLoanIndividualMonitoringData instance(final Long id, final Long loanId, final BigDecimal totalLoanAmount,
            final Long clientId, final String clientName, final String clientExternalID, final BigDecimal proposedAmount,
            final BigDecimal approvedAmount, final BigDecimal disbursedAmount, final CodeValueData loanPurpose,
            final Boolean isClientSelected, final BigDecimal chargeAmount, final BigDecimal adjustedAmount,
            final BigDecimal installmentAmount, final BigDecimal totalPaybleAmount, final BigDecimal paidInterestAmount,
            final BigDecimal paidAmount, final BigDecimal interestAmount, final BigDecimal paidPrincipalAmount,
            final BigDecimal paidChargeAmount, final BigDecimal waivedInterestAmount, final BigDecimal waivedChargeAmount,
            BigDecimal principalWrittenOffAmount, BigDecimal interestWrittenOffAmount, BigDecimal chargeWrittenOffAmount,
            BigDecimal penaltyAmount, BigDecimal paidPenaltyAmount, BigDecimal waivedPenaltyAmount, BigDecimal penaltywrittenOffAmount,
            final BigDecimal remainingTransactionAmount, BigDecimal transactionAmount, Boolean isActive,
            BigDecimal totalPrincipalOutstanding, BigDecimal totalInterestOutstanding, BigDecimal totalFeeChargeOutstanding,
            BigDecimal totalpenaltyChargeOutstanding, BigDecimal totalLoanOutstandingAmount) {

        return new GroupLoanIndividualMonitoringData(id, loanId, totalLoanAmount, clientId, clientName, clientExternalID, proposedAmount,
                approvedAmount, disbursedAmount, loanPurpose, isClientSelected, chargeAmount, adjustedAmount, installmentAmount,
                totalPaybleAmount, paidInterestAmount, paidAmount, interestAmount, paidPrincipalAmount, paidChargeAmount,
                waivedInterestAmount, waivedChargeAmount, principalWrittenOffAmount, interestWrittenOffAmount, chargeWrittenOffAmount,
                penaltyAmount, paidPenaltyAmount, waivedPenaltyAmount, penaltywrittenOffAmount, remainingTransactionAmount,
                transactionAmount, isActive, totalPrincipalOutstanding, totalInterestOutstanding, totalFeeChargeOutstanding,
                totalpenaltyChargeOutstanding, totalLoanOutstandingAmount);
    }

    public static GroupLoanIndividualMonitoringData waiveInterestDetails(final Long id, final Long clientId, final String clientName,
            final BigDecimal paidInterestAmount, final BigDecimal interestAmount, BigDecimal remainingTransactionAmount,
            BigDecimal transactionAmount, Boolean isClientSelected) {
        final Long loanId = null;
        final BigDecimal totalLoanAmount = null;
        BigDecimal proposedAmount = null;
        BigDecimal approvedAmount = null;
        BigDecimal disbursedAmount = null;
        CodeValueData loanPurpose = null;
        BigDecimal chargeAmount = null;
        BigDecimal adjustedAmount = null;
        BigDecimal installmentAmount = null;
        BigDecimal totalPaybleAmount = null;
        BigDecimal paidAmount = null;
        BigDecimal paidPrincipalAmount = null;
        BigDecimal paidChargeAmount = null;
        BigDecimal waivedInterestAmount = null;
        BigDecimal waivedChargeAmount = null;
        BigDecimal principalWrittenOffAmount = null;
        BigDecimal interestWrittenOffAmount = null;
        BigDecimal chargeWrittenOffAmount = null;
        BigDecimal penaltyAmount = null;
        BigDecimal paidPenaltyAmount = null;
        BigDecimal waivedPenaltyAmount = null;
        BigDecimal penaltywrittenOffAmount = null;
        String clientExternalID = null;
        Boolean isActive = null;
        BigDecimal totalPrincipalOutstanding = null;
        BigDecimal totalInterestOutstanding = null;
        BigDecimal totalFeeChargeOutstanding = null;
        BigDecimal totalpenaltyChargeOutstanding = null;
        BigDecimal totalLoanOutstandingAmount = null;
        return new GroupLoanIndividualMonitoringData(id, loanId, totalLoanAmount, clientId, clientName, clientExternalID, proposedAmount,
                approvedAmount, disbursedAmount, loanPurpose, isClientSelected, chargeAmount, adjustedAmount, installmentAmount,
                totalPaybleAmount, paidInterestAmount, paidAmount, interestAmount, paidPrincipalAmount, paidChargeAmount,
                waivedInterestAmount, waivedChargeAmount, principalWrittenOffAmount, interestWrittenOffAmount, chargeWrittenOffAmount,
                penaltyAmount, paidPenaltyAmount, waivedPenaltyAmount, penaltywrittenOffAmount, remainingTransactionAmount,
                transactionAmount, isActive, totalPrincipalOutstanding, totalInterestOutstanding, totalFeeChargeOutstanding,
                totalpenaltyChargeOutstanding, totalLoanOutstandingAmount);
    }

    public BigDecimal getInstallmentAmount() {
        return this.installmentAmount;
    }

    public BigDecimal getChargeAmount() {
        return this.chargeAmount;
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public BigDecimal getInterestAmount() {
        return this.interestAmount;
    }

    public void setInterestAmount(BigDecimal interestAmount) {
        this.interestAmount = interestAmount;
    }

    public Long getId() {
        return this.id;
    }

    public BigDecimal getDisbursedAmount() {
        return this.disbursedAmount;
    }

    public void setDisbursedAmount(BigDecimal disbursedAmount) {
        this.disbursedAmount = disbursedAmount;
    }

    public BigDecimal getPaidChargeAmount() {
        return this.paidChargeAmount;
    }

    public void setPaidChargeAmount(BigDecimal paidChargeAmount) {
        this.paidChargeAmount = paidChargeAmount;
    }

    public BigDecimal getWaivedChargeAmount() {
        return this.waivedChargeAmount;
    }

    public void setWaivedChargeAmount(BigDecimal waivedChargeAmount) {
        this.waivedChargeAmount = waivedChargeAmount;
    }

    public BigDecimal getTransactionAmount() {
        return this.transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public BigDecimal getRemainingTransactionAmount() {
        return this.remainingTransactionAmount;
    }

    public void setRemainingTransactionAmount(BigDecimal remainingTransactionAmount) {
        this.remainingTransactionAmount = remainingTransactionAmount;
    }

    public BigDecimal getTotalPaybleAmount() {
        return this.totalPaybleAmount;
    }

    public void setTotalPaybleAmount(BigDecimal totalPaybleAmount) {
        this.totalPaybleAmount = totalPaybleAmount;
    }

    public BigDecimal getPaidInterestAmount() {
        return this.paidInterestAmount;
    }

    public void setPaidInterestAmount(BigDecimal paidInterestAmount) {
        this.paidInterestAmount = paidInterestAmount;
    }

    public BigDecimal getWaivedInterestAmount() {
        return this.waivedInterestAmount;
    }

    public void setWaivedInterestAmount(BigDecimal waivedInterestAmount) {
        this.waivedInterestAmount = waivedInterestAmount;
    }

    public BigDecimal getPaidAmount() {
        return this.paidAmount;
    }

    public void setInstallmentAmount(BigDecimal installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public Boolean getIsClientSelected() {
        return this.isClientSelected;
    }

    public void setIsClientSelected(Boolean isClientSelected) {
        this.isClientSelected = isClientSelected;
    }

    public BigDecimal getPaidPrincipalAmount() {
        return this.paidPrincipalAmount;
    }

    public BigDecimal getPrincipalWrittenOffAmount() {
        return principalWrittenOffAmount;
    }

    public void setPrincipalWrittenOffAmount(BigDecimal principalWrittenOffAmount) {
        this.principalWrittenOffAmount = principalWrittenOffAmount;
    }

    public BigDecimal getInterestWrittenOffAmount() {
        return interestWrittenOffAmount;
    }

    public void setInterestWrittenOffAmount(BigDecimal interestWrittenOffAmount) {
        this.interestWrittenOffAmount = interestWrittenOffAmount;
    }

    public BigDecimal getChargeWrittenOffAmount() {
        return chargeWrittenOffAmount;
    }

    public void setChargeWrittenOffAmount(BigDecimal chargeWrittenOffAmount) {
        this.chargeWrittenOffAmount = chargeWrittenOffAmount;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
