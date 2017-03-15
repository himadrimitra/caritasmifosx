/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.domain;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.portfolio.charge.domain.GroupLoanIndividualMonitoringCharge;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.springframework.data.jpa.domain.AbstractPersistable;

@SuppressWarnings("serial")
@Entity
@Table(name = "m_loan_glim")
public class GroupLoanIndividualMonitoring extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "loan_id")
    private Loan loan;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "proposed_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal proposedAmount;

    @Column(name = "approved_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal approvedAmount;

    @Column(name = "disbursed_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal disbursedAmount;

    @ManyToOne
    @JoinColumn(name = "loanpurpose_cv_id", nullable = true)
    private CodeValue loanPurpose;

    @Column(name = "is_client_selected", nullable = true)
    private Boolean isClientSelected;

    @Column(name = "charge_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal chargeAmount;

    @Column(name = "adjusted_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal adjustedAmount;

    @Column(name = "installment_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal installmentAmount;

    @Column(name = "total_payble_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal totalPaybleAmount;

    @Column(name = "paid_interest_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal paidInterestAmount;

    @Column(name = "paid_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal totalPaidAmount;

    @Column(name = "interest_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal interestAmount;

    @Column(name = "percentage", scale = 6, precision = 19, nullable = true)
    private BigDecimal percentage;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "groupLoanIndividualMonitoring", orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<GroupLoanIndividualMonitoringCharge> groupLoanIndividualMonitoringCharge = new HashSet<>();

    @Column(name = "paid_principal_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal paidPrincipalAmount;

    @Column(name = "paid_charge_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal paidChargeAmount;

    @Column(name = "waived_interest_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal waivedInterestAmount;

    @Column(name = "waived_charge_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal waivedChargeAmount;

    @Column(name = "principal_writtenoff_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal principalWrittenOffAmount;

    @Column(name = "interest_writtenoff_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal interestWrittenOffAmount;

    @Column(name = "fee_charges_writtenoff_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal chargeWrittenOffAmount;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "groupLoanIndividualMonitoring", orphanRemoval = true)
    private Set<GroupLoanIndividualMonitoringTransaction> groupLoanIndividualMonitoringTransactions = new HashSet<>();

    @Transient
    private BigDecimal transactionAmount;

    @Transient
    private Map<String, BigDecimal> processedTransactionMap = new HashMap<>();

    @Column(name = "is_active", nullable = true)
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "writeoff_reason_cv_id", nullable = true)
    private CodeValue writeOffReason;
    
    @Column(name = "overpaid_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal overpaidAmount;

    public GroupLoanIndividualMonitoring() {

    }

    public GroupLoanIndividualMonitoring(final Loan loan, final Client client) {
        this.loan = loan;
        this.client = client;
    }

    public GroupLoanIndividualMonitoring(final Loan loan, final Client client, final BigDecimal proposedAmount,
            final BigDecimal approvedAmount, final BigDecimal disbursedAmount, final CodeValue loanPurpose, final Boolean isClientSelected,
            final Set<GroupLoanIndividualMonitoringCharge> groupLoanIndividualMonitoringCharge, final BigDecimal percentage,
            BigDecimal installmentAmount) {
        this.loan = loan;
        this.client = client;
        this.proposedAmount = proposedAmount;
        this.approvedAmount = approvedAmount;
        this.disbursedAmount = disbursedAmount;
        this.loanPurpose = loanPurpose;
        this.isClientSelected = isClientSelected;
        this.chargeAmount = null;
        this.paidInterestAmount = null;
        this.totalPaybleAmount = null;
        this.installmentAmount = installmentAmount;
        this.adjustedAmount = null;
        this.totalPaidAmount = null;
        this.interestAmount = null;
        this.groupLoanIndividualMonitoringCharge = groupLoanIndividualMonitoringCharge;
        this.percentage = null;
        this.paidPrincipalAmount = null;
        this.paidChargeAmount = null;
        this.waivedInterestAmount = null;
        this.waivedChargeAmount = null;
        this.chargeWrittenOffAmount = null;
        this.interestWrittenOffAmount = null;
        this.principalWrittenOffAmount = null;
        this.isActive = true;
    }

    public static GroupLoanIndividualMonitoring createDefaultInstance(final Loan loan, final Client client) {
        return new GroupLoanIndividualMonitoring(loan, client);
    }

    public static GroupLoanIndividualMonitoring createInstance(final Loan loan, final Client client, final BigDecimal proposedAmount,
            final BigDecimal approvedAmount, final BigDecimal disbursedAmount, final CodeValue loanPurpose, final Boolean isClientSelected,
            Set<GroupLoanIndividualMonitoringCharge> clientCharges, BigDecimal installmentAmount) {
        BigDecimal percentage = null;
        return new GroupLoanIndividualMonitoring(loan, client, proposedAmount, approvedAmount, disbursedAmount, loanPurpose,
                isClientSelected, clientCharges, percentage, installmentAmount);
    }

    public static GroupLoanIndividualMonitoring createInstance(final Loan loan, final Client client, final BigDecimal proposedAmount,
            final BigDecimal approvedAmount, final BigDecimal disbursedAmount, final CodeValue loanPurpose, final Boolean isClientSelected,
            final BigDecimal adjustedAmount, final BigDecimal installmentAmount, final BigDecimal totalPaybleAmount,
            final BigDecimal paidInterestAmount, final BigDecimal totalPaidAmount, final BigDecimal interestAmount,
            final Set<GroupLoanIndividualMonitoringCharge> groupLoanIndividualMonitoringCharges, final BigDecimal percentage,
            final BigDecimal paidPrincipalAmount, final BigDecimal paidChargeAmount, final BigDecimal waivedInterestAmount,
            final BigDecimal waivedChargeAmount) {
        final BigDecimal chargeAmount = null;
        final BigDecimal principalWrittenOffAmount = null;
        final BigDecimal interestWrittenOffAmount = null;
        final BigDecimal chargeWrittenOffAmount = null;
        final boolean isActive = true;
        return new GroupLoanIndividualMonitoring(loan, client, proposedAmount, approvedAmount, disbursedAmount, loanPurpose,
                isClientSelected, chargeAmount, adjustedAmount, installmentAmount, totalPaybleAmount, paidInterestAmount, totalPaidAmount,
                interestAmount, groupLoanIndividualMonitoringCharges, percentage, paidPrincipalAmount, paidChargeAmount,
                waivedInterestAmount, waivedChargeAmount, principalWrittenOffAmount, interestWrittenOffAmount, chargeWrittenOffAmount,
                isActive);
    }

    public GroupLoanIndividualMonitoring(final Loan loan, final Client client, final BigDecimal proposedAmount,
            final BigDecimal approvedAmount, final BigDecimal disbursedAmount, final CodeValue loanPurpose, final Boolean isClientSelected,
            final BigDecimal chargeAmount, final BigDecimal adjustedAmount, final BigDecimal installmentAmount,
            final BigDecimal totalPaybleAmount, final BigDecimal paidInterestAmount, final BigDecimal paidAmount,
            final BigDecimal interestAmount, final Set<GroupLoanIndividualMonitoringCharge> groupLoanIndividualMonitoringCharge,
            final BigDecimal percentage, final BigDecimal paidPrincipalAmount, final BigDecimal paidChargeAmount,
            final BigDecimal waivedInterestAmount, final BigDecimal waivedChargeAmount, final BigDecimal principalWrittenOffAmount,
            final BigDecimal interestWrittenOffAmount, final BigDecimal chargeWrittenOffAmount, final Boolean isActive) {
        this.loan = loan;
        this.client = client;
        this.proposedAmount = proposedAmount;
        this.approvedAmount = approvedAmount;
        this.disbursedAmount = disbursedAmount;
        this.loanPurpose = loanPurpose;
        this.isClientSelected = isClientSelected;
        this.chargeAmount = chargeAmount;
        this.adjustedAmount = adjustedAmount;
        this.installmentAmount = installmentAmount;
        this.totalPaybleAmount = totalPaybleAmount;
        this.paidInterestAmount = paidInterestAmount;
        this.totalPaidAmount = paidAmount;
        this.interestAmount = interestAmount;
        this.groupLoanIndividualMonitoringCharge = groupLoanIndividualMonitoringCharge;
        this.percentage = percentage;
        this.paidPrincipalAmount = paidPrincipalAmount;
        this.paidChargeAmount = paidChargeAmount;
        this.waivedInterestAmount = waivedInterestAmount;
        this.waivedChargeAmount = waivedChargeAmount;
        this.chargeWrittenOffAmount = chargeWrittenOffAmount;
        this.interestWrittenOffAmount = interestWrittenOffAmount;
        this.principalWrittenOffAmount = principalWrittenOffAmount;
        this.isActive = isActive;
    }

    public Loan getLoan() {
        return this.loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public Client getClient() {
        return this.client;
    }

    public BigDecimal getProposedAmount() {
        return this.proposedAmount;
    }

    public void setProposedAmount(BigDecimal proposedAmount) {
        this.proposedAmount = proposedAmount;
    }

    public BigDecimal getApprovedAmount() {
        return this.approvedAmount;
    }

    public void setApprovedAmount(BigDecimal approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public BigDecimal getDisbursedAmount() {
        return this.disbursedAmount;
    }

    public void setDisbursedAmount(BigDecimal disbursedAmount) {
        this.disbursedAmount = disbursedAmount;
    }

    public CodeValue getLoanPurpose() {
        return this.loanPurpose;
    }

    public void setLoanPurpose(CodeValue loanPurpose) {
        this.loanPurpose = loanPurpose;
    }

    public Boolean isClientSelected() {
        return isClientSelected;
    }

    public void setIsClientSelected(Boolean isClientSelected) {
        this.isClientSelected = isClientSelected;
    }

    public BigDecimal getChargeAmount() {
        return this.chargeAmount;
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public BigDecimal getAdjustedAmount() {
        return this.adjustedAmount;
    }

    public void setAdjustedAmount(BigDecimal adjustedAmount) {
        this.adjustedAmount = adjustedAmount;
    }

    public BigDecimal getInstallmentAmount() {
        return this.installmentAmount;
    }

    public void setInstallmentAmount(BigDecimal installmentAmount) {
        this.installmentAmount = installmentAmount;
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

    public void setClient(Client client) {
        this.client = client;
    }

    public BigDecimal getTotalPaidAmount() {
        return this.totalPaidAmount;
    }

    public void setPaidAmount(BigDecimal totalPaidAmount) {
        this.totalPaidAmount = totalPaidAmount;
    }

    public BigDecimal getInterestAmount() {
        return this.interestAmount;
    }

    public void setInterestAmount(BigDecimal interestAmount) {
        this.interestAmount = interestAmount;
    }

    public Set<GroupLoanIndividualMonitoringCharge> getGroupLoanIndividualMonitoringCharges() {
        return this.groupLoanIndividualMonitoringCharge;
    }

    public Set<GroupLoanIndividualMonitoringTransaction> getGroupLoanIndividualMonitoringTransaction() {
        return this.groupLoanIndividualMonitoringTransactions;
    }

    public void updatePercentage(BigDecimal updatedPercentage) {
        this.percentage = updatedPercentage;
    }

    public BigDecimal getPaidPrincipalAmount() {
        return this.paidPrincipalAmount;
    }

    public void setPaidPrincipalAmount(BigDecimal paidPrincipalAmount) {
        this.paidPrincipalAmount = paidPrincipalAmount;
    }

    public BigDecimal getPaidChargeAmount() {
        return this.paidChargeAmount;
    }

    public void setPaidChargeAmount(BigDecimal paidChargeAmount) {
        this.paidChargeAmount = paidChargeAmount;
    }

    public BigDecimal getWaivedInterestAmount() {
        return this.waivedInterestAmount;
    }

    public void setWaivedInterestAmount(BigDecimal waivedInterestAmount) {
        this.waivedInterestAmount = waivedInterestAmount;
    }

    public BigDecimal getWaivedChargeAmount() {
        return this.waivedChargeAmount;
    }

    public void setWaivedChargeAmount(BigDecimal waivedChargeAmount) {
        this.waivedChargeAmount = waivedChargeAmount;
    }

    public void updateGlimTransaction(final GroupLoanIndividualMonitoringTransaction glimTransaction) {
        this.groupLoanIndividualMonitoringTransactions.add(glimTransaction);
    }

    public void undoGlimTransaction() {
        this.groupLoanIndividualMonitoringTransactions.clear();
    }

    public void resetDerievedComponents() {
        this.paidChargeAmount = BigDecimal.ZERO;
        this.paidInterestAmount = BigDecimal.ZERO;
        this.paidPrincipalAmount = BigDecimal.ZERO;
        this.totalPaidAmount = BigDecimal.ZERO;
        this.waivedChargeAmount = BigDecimal.ZERO;
        this.waivedInterestAmount = BigDecimal.ZERO;
        this.principalWrittenOffAmount = BigDecimal.ZERO;
        this.interestWrittenOffAmount = BigDecimal.ZERO;
        this.chargeWrittenOffAmount = BigDecimal.ZERO;
        this.isActive = true;
        this.disbursedAmount = this.approvedAmount;
        this.overpaidAmount = BigDecimal.ZERO;

    }

    public void updateTransactionAmount(final BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public BigDecimal getTransactionAmount() {
        return this.transactionAmount;
    }

    public void updateProcessedTransactionMap(Map<String, BigDecimal> processedTransactionMap) {
        this.processedTransactionMap = processedTransactionMap;
    }

    public Map<String, BigDecimal> getProcessedTransactionMap() {
        return this.processedTransactionMap;
    }

    public boolean isWrittenOff() {
        boolean isWrittenOff = false;
        if (MathUtility.isGreaterThanZero(this.principalWrittenOffAmount) || MathUtility.isGreaterThanZero(this.interestWrittenOffAmount)
                || MathUtility.isGreaterThanZero(this.chargeWrittenOffAmount)) {
            isWrittenOff = true;
        }
        return isWrittenOff;
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

    public CodeValue getWriteOffReason() {
        return this.writeOffReason;
    }

    public void setWriteOffReason(CodeValue writeOffReason) {
        this.writeOffReason = writeOffReason;
    }

    public boolean isOutstandingBalanceZero() {
        boolean isOutstandingBalanceZero = false;
        BigDecimal principalAmount = this.proposedAmount;
        if (this.disbursedAmount != null) {
            principalAmount = this.disbursedAmount;
        } else if (this.approvedAmount != null) {
            principalAmount = this.approvedAmount;
        }
        if (principalAmount != null
                && principalAmount.compareTo(MathUtility.add(this.paidPrincipalAmount, this.principalWrittenOffAmount)) == 0
                && this.interestAmount.compareTo(MathUtility.add(this.paidInterestAmount, this.waivedInterestAmount,
                        this.interestWrittenOffAmount)) == 0
                && this.chargeAmount.compareTo(MathUtility.add(this.paidChargeAmount, this.waivedChargeAmount,
                        this.chargeWrittenOffAmount)) == 0) {
            isOutstandingBalanceZero = true;

        }
        
        return isOutstandingBalanceZero;
    }

    
    public BigDecimal getOverpaidAmount() {
        return this.overpaidAmount;
    }

    
    public void setOverpaidAmount(BigDecimal overpaidAmount) {
        this.overpaidAmount = overpaidAmount;
    }
    
    
}
