/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.loanaccount.guarantor.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.fineract.portfolio.account.domain.AccountAssociations;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_guarantor_funding_details")
public class GuarantorFundingDetails extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "guarantor_id", nullable = false)
    private Guarantor guarantor;

    @ManyToOne
    @JoinColumn(name = "account_associations_id", nullable = false)
    private AccountAssociations accountAssociations;

    @Column(name = "status_enum", nullable = false)
    private Integer status;

    @Column(name = "amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal amount;

    @Column(name = "amount_released_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal amountReleased;

    @Column(name = "amount_remaining_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal amountRemaining;

    @Column(name = "amount_transfered_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal amountTransfered;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "guarantorFundingDetails", orphanRemoval = true)
    private final List<GuarantorFundingTransaction> guarantorFundingTransactions = new ArrayList<>();

    protected GuarantorFundingDetails() {}

    public GuarantorFundingDetails(final AccountAssociations accountAssociations, final Integer status, final BigDecimal amount) {
        this.accountAssociations = accountAssociations;
        this.status = status;
        this.amount = amount;
        this.amountRemaining = amount;
    }

    public void updateGuarantor(final Guarantor guarantor) {
        this.guarantor = guarantor;
    }

    public void updateStatus(final GuarantorFundStatusType guarantorFundStatusType) {
        this.status = guarantorFundStatusType.getValue();
    }

    public GuarantorFundStatusType getStatus() {
        return GuarantorFundStatusType.fromInt(this.status);
    }

    public SavingsAccount getLinkedSavingsAccount() {
        return this.accountAssociations.linkedSavingsAccount();
    }

    public Loan getLoanAccount() {
        return this.guarantor.getLoan();
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmountRemaining(final BigDecimal amountRemaining) {
        this.amountRemaining = amountRemaining;
    }

    public BigDecimal getAmountReleased() {
        return this.amountReleased == null ? BigDecimal.ZERO : this.amountReleased;
    }

    public BigDecimal getAmountRemaining() {
        return this.amountRemaining == null ? BigDecimal.ZERO : this.amountRemaining;
    }

    public BigDecimal getAmountTransfered() {
        return this.amountTransfered == null ? BigDecimal.ZERO : this.amountTransfered;
    }

    public void releaseFunds(final BigDecimal amount) {
        this.amountReleased = getAmountReleased().add(amount);
        this.amountRemaining = getAmountRemaining().subtract(amount);
        if (this.amountRemaining.setScale(2, RoundingMode.FLOOR).compareTo(BigDecimal.ZERO) == 0) {
            updateStatus(GuarantorFundStatusType.COMPLETED);
            this.accountAssociations.setActive(false);
        }
    }

    public void undoReleaseFunds(final BigDecimal amount) {
        this.amountReleased = getAmountReleased().subtract(amount);
        this.amountRemaining = getAmountRemaining().add(amount);
        if (getStatus().isCompleted() && this.amountRemaining.setScale(2, RoundingMode.FLOOR).compareTo(BigDecimal.ZERO) == 1) {
            updateStatus(GuarantorFundStatusType.ACTIVE);
            this.accountAssociations.setActive(true);
        }
    }

    public void withdrawFunds(final BigDecimal amount) {
        this.amountTransfered = amount;
    }

    public void addGuarantorFundingTransactions(final GuarantorFundingTransaction guarantorFundingTransaction) {
        this.guarantorFundingTransactions.add(guarantorFundingTransaction);
    }

    public void undoAllTransactions() {
        for (final GuarantorFundingTransaction fundingTransaction : this.guarantorFundingTransactions) {
            fundingTransaction.reverseTransaction();
        }
        this.accountAssociations.setActive(false);
    }

    public void addSelfAmmount(final BigDecimal amount, final BigDecimal loanAmount) {
        final BigDecimal remainingAmount = getAmountRemaining();
        BigDecimal amountDiff = BigDecimal.ZERO;
        if (remainingAmount.compareTo(loanAmount) < 1) {
            final BigDecimal tempRemainingAmount = getAmountRemaining().add(amount);
            if (tempRemainingAmount.compareTo(loanAmount) == 1) {
                amountDiff = loanAmount.subtract(remainingAmount);
                this.amountRemaining = loanAmount;
            } else {
                this.amountRemaining = getAmountRemaining().add(amount);
                amountDiff = amount;
            }
            this.amount = this.amount.add(amountDiff);
        }
    }

    public AccountAssociations getAccountAssociations() {
        return this.accountAssociations;
    }

    public void setAccountAssociations(final AccountAssociations accountAssociations) {
        this.accountAssociations = accountAssociations;
    }
}