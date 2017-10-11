/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.charge.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoring;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_loan_glim_charges")
public class GroupLoanIndividualMonitoringCharge extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "glim_id")
    private GroupLoanIndividualMonitoring groupLoanIndividualMonitoring;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "charge_id")
    private Charge charge;

    @Column(name = "fee_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal feeAmount;

    @Column(name = "revised_fee_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal revisedFeeAmount;

    @Transient
    private boolean emiRoundingGoalSeek;

    @Column(name = "waived_charge_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal waivedChargeAmount;

    @Column(name = "paid_charge", scale = 6, precision = 19, nullable = true)
    private BigDecimal paidCharge;

    public GroupLoanIndividualMonitoringCharge(final GroupLoanIndividualMonitoring glim, final Client client, final Charge charge,
            final BigDecimal feeAmount, final BigDecimal revisedFeeAmount, final boolean emiRoundingGoalSeek,
            final BigDecimal waivedChargeAmount, final BigDecimal paidCharge) {
        this.groupLoanIndividualMonitoring = glim;
        this.client = client;
        this.charge = charge;
        this.feeAmount = feeAmount;
        this.revisedFeeAmount = revisedFeeAmount;
        this.emiRoundingGoalSeek = emiRoundingGoalSeek;
        this.waivedChargeAmount = waivedChargeAmount;
        this.paidCharge = paidCharge;
    }

    public GroupLoanIndividualMonitoringCharge() {

    }

    public static GroupLoanIndividualMonitoringCharge instance(final GroupLoanIndividualMonitoring glim, final Client client,
            final Charge charge, final BigDecimal feeAmount, final BigDecimal revisedFeeAmount, final boolean emiRoundingGoalSeek,
            final BigDecimal waivedChargeAmount, final BigDecimal paidCharge) {
        return new GroupLoanIndividualMonitoringCharge(glim, client, charge, feeAmount, revisedFeeAmount, emiRoundingGoalSeek,
                waivedChargeAmount, paidCharge);
    }

    public GroupLoanIndividualMonitoring getGlim() {
        return this.groupLoanIndividualMonitoring;
    }

    public void setGlim(GroupLoanIndividualMonitoring glim) {
        this.groupLoanIndividualMonitoring = glim;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Charge getCharge() {
        return this.charge;
    }

    public void setCharge(Charge charge) {
        this.charge = charge;
    }

    public BigDecimal getFeeAmount() {
        return this.feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public BigDecimal getRevisedFeeAmount() {
        return this.revisedFeeAmount;
    }

    public void setRevisedFeeAmount(BigDecimal revisedFeeAmount) {
        this.revisedFeeAmount = revisedFeeAmount;
    }

    public boolean isEmiRoundingGoalSeek() {
        return this.emiRoundingGoalSeek;
    }

    public GroupLoanIndividualMonitoring getGroupLoanIndividualMonitoring() {
        return this.groupLoanIndividualMonitoring;
    }

    public void setGroupLoanIndividualMonitoring(GroupLoanIndividualMonitoring groupLoanIndividualMonitoring) {
        this.groupLoanIndividualMonitoring = groupLoanIndividualMonitoring;
    }

    public BigDecimal getWaivedChargeAmount() {
        return this.waivedChargeAmount;
    }

    public void setWaivedChargeAmount(BigDecimal waivedChargeAmount) {
        this.waivedChargeAmount = waivedChargeAmount;
    }

    public BigDecimal getPaidCharge() {
        return this.paidCharge;
    }

    public void setPaidCharge(BigDecimal paidCharge) {
        this.paidCharge = paidCharge;
    }

}
