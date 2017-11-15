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
package org.apache.fineract.portfolio.loanaccount.domain;

import java.math.BigDecimal;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_loan_installment_charge")
public class LoanInstallmentCharge extends AbstractPersistable<Long> implements Comparable<LoanInstallmentCharge> {

    @ManyToOne
    @JoinColumn(name = "loan_charge_id", nullable = false)
    private LoanCharge loancharge;

    @ManyToOne
    @JoinColumn(name = "loan_schedule_id", nullable = false)
    private LoanRepaymentScheduleInstallment installment;

    @Column(name = "amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal amount;

    @Column(name = "amount_paid_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal amountPaid;

    @Column(name = "amount_waived_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal amountWaived;

    @Column(name = "amount_writtenoff_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal amountWrittenOff;

    @Column(name = "amount_outstanding_derived", scale = 6, precision = 19, nullable = false)
    private BigDecimal amountOutstanding;

    @Column(name = "amount_through_charge_payment", scale = 6, precision = 19, nullable = true)
    private BigDecimal amountThroughChargePayment;

    @Column(name = "is_paid_derived", nullable = false)
    private boolean paid = false;

    @Column(name = "waived", nullable = false)
    private boolean waived = false;

    @Column(name = "amount_sans_tax", scale = 6, precision = 19, nullable = true)
    private BigDecimal amountSansTax;

    @Column(name = "tax_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal taxAmount;

    public LoanInstallmentCharge() {
        // TODO Auto-generated constructor stub
    }

    public LoanInstallmentCharge(final BigDecimal amount, final LoanCharge loanCharge, final LoanRepaymentScheduleInstallment installment) {
        this.loancharge = loanCharge;
        this.installment = installment;
        this.amount = amount;
        this.amountOutstanding = amount;
        this.amountPaid = null;
        this.amountWaived = null;
        this.amountWrittenOff = null;

        if (this.loancharge.getTaxGroup() != null) {
            final Map<String, BigDecimal> loanChargeTaxSplitDetails = this.loancharge.updateLoanChargeTaxDetails(installment.getDueDate(),
                    this.amount);
            if (!loanChargeTaxSplitDetails.isEmpty()) {
                for (final Map.Entry<String, BigDecimal> mapEntry : loanChargeTaxSplitDetails.entrySet()) {
                    if (mapEntry.getKey().equalsIgnoreCase("incomeAmount")) {
                        updateAmountSansTax(mapEntry.getValue());
                    }
                    if (mapEntry.getKey().equalsIgnoreCase("taxAmount")) {
                        updateTaxAmount(mapEntry.getValue());
                    }
                }
            }
        }
    }

    public static LoanInstallmentCharge copyLoanInstallmentCharge(final LoanInstallmentCharge loanInstallmentCharge,
            final LoanCharge loancharge) {
        return new LoanInstallmentCharge(loanInstallmentCharge, loancharge);
    }

    private LoanInstallmentCharge(final LoanInstallmentCharge loanInstallmentCharge, final LoanCharge loancharge) {
        this.loancharge = loancharge;
        this.installment = loanInstallmentCharge.installment;
        this.amount = loanInstallmentCharge.amount;
        this.amountOutstanding = loanInstallmentCharge.amountOutstanding;
        this.amountPaid = loanInstallmentCharge.amountPaid;
        this.amountWaived = loanInstallmentCharge.amountWaived;
        this.amountWrittenOff = loanInstallmentCharge.amountWrittenOff;
        this.paid = loanInstallmentCharge.paid;
        this.waived = loanInstallmentCharge.waived;
        this.amountThroughChargePayment = loanInstallmentCharge.amountThroughChargePayment;
        this.amountSansTax = loanInstallmentCharge.amountSansTax;
        this.taxAmount = loanInstallmentCharge.taxAmount;
    }

    private void updateTaxAmount(final BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    private void updateAmountSansTax(final BigDecimal amountSansTax) {
        this.amountSansTax = amountSansTax;
    }

    public void copyFrom(final LoanInstallmentCharge loanChargePerInstallment) {
        this.amount = loanChargePerInstallment.amount;
        this.installment = loanChargePerInstallment.installment;
        this.amountOutstanding = calculateOutstanding();
        this.paid = determineIfFullyPaid();
    }

    public Money waive(final MonetaryCurrency currency) {
        this.amountWaived = this.amountOutstanding;
        this.amountOutstanding = BigDecimal.ZERO;
        this.paid = false;
        this.waived = true;
        return getAmountWaived(currency);
    }

    public Money getAmountWaived(final MonetaryCurrency currency) {
        return Money.of(currency, this.amountWaived);
    }

    private boolean determineIfFullyPaid() {
        if (this.amount == null) { return true; }
        return BigDecimal.ZERO.compareTo(calculateOutstanding()) == 0;
    }

    private BigDecimal calculateOutstanding() {
        if (this.amount == null) { return null; }
        BigDecimal amountPaidLocal = BigDecimal.ZERO;
        if (this.amountPaid != null) {
            amountPaidLocal = this.amountPaid;
        }

        BigDecimal amountWaivedLocal = BigDecimal.ZERO;
        if (this.amountWaived != null) {
            amountWaivedLocal = this.amountWaived;
        }

        BigDecimal amountWrittenOffLocal = BigDecimal.ZERO;
        if (this.amountWrittenOff != null) {
            amountWrittenOffLocal = this.amountWrittenOff;
        }

        final BigDecimal totalAccountedFor = amountPaidLocal.add(amountWaivedLocal).add(amountWrittenOffLocal);

        return this.amount.subtract(totalAccountedFor);
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Money getAmount(final MonetaryCurrency currency) {
        return Money.of(currency, this.amount);
    }

    public Money getAmountPaid(final MonetaryCurrency currency) {
        return Money.of(currency, this.amountPaid);
    }

    public BigDecimal getAmountOutstanding() {
        return this.amountOutstanding;
    }

    public Money getAmountOutstanding(final MonetaryCurrency currency) {
        return Money.of(currency, this.amountOutstanding);
    }

    private BigDecimal calculateAmountOutstanding(final MonetaryCurrency currency) {
        return getAmount(currency).minus(getAmountWaived(currency)).minus(getAmountPaid(currency)).minus(getAmountWrittenOff(currency))
                .getAmount();
    }

    public boolean isPaid() {
        return this.paid;
    }

    public boolean isWaived() {
        return this.waived;
    }

    public boolean isPending() {
        return !(isPaid() || isWaived());
    }

    public boolean isChargeAmountpaid(final MonetaryCurrency currency) {
        final Money amounPaidThroughChargePayment = Money.of(currency, this.amountThroughChargePayment);
        final Money paid = Money.of(currency, this.amountPaid);
        return amounPaidThroughChargePayment.isEqualTo(paid);
    }

    public LoanRepaymentScheduleInstallment getRepaymentInstallment() {
        return this.installment;
    }

    public Money updatePaidAmountBy(final Money incrementBy, final Money feeAmount) {

        Money amountPaidToDate = Money.of(incrementBy.getCurrency(), this.amountPaid);
        final Money amountOutstanding = Money.of(incrementBy.getCurrency(), this.amountOutstanding);
        final Money amountPaidPreviously = amountPaidToDate;
        Money amountPaidOnThisCharge = Money.zero(incrementBy.getCurrency());
        // this.loancharge.getLoan().isGLIMLoan()
        if (incrementBy.isGreaterThanOrEqualTo(amountOutstanding)) {
            amountPaidOnThisCharge = amountOutstanding;
            amountPaidToDate = amountPaidToDate.plus(amountOutstanding);
            this.amountPaid = amountPaidToDate.getAmount();
            this.amountOutstanding = BigDecimal.ZERO;
        } else {
            amountPaidOnThisCharge = incrementBy;
            amountPaidToDate = amountPaidToDate.plus(incrementBy);
            this.amountPaid = amountPaidToDate.getAmount();
            this.amountOutstanding = calculateAmountOutstanding(incrementBy.getCurrency());
        }
        Money amountFromChargePayment = Money.of(incrementBy.getCurrency(), this.amountThroughChargePayment);
        if (amountPaidPreviously.isGreaterThanZero()) {
            amountFromChargePayment = amountFromChargePayment.plus(feeAmount);
        } else {
            amountFromChargePayment = feeAmount;
        }
        this.amountThroughChargePayment = amountFromChargePayment.getAmount();
        if (determineIfFullyPaid()) {
            final Money waivedAmount = getAmountWaived(incrementBy.getCurrency());
            if (waivedAmount.isGreaterThanZero()) {
                this.waived = true;
            } else {
                this.paid = true;
            }
        }

        return amountPaidOnThisCharge;
    }

    public Money getAmountWrittenOff(final MonetaryCurrency currency) {
        return Money.of(currency, this.amountWrittenOff);
    }

    public void resetPaidAmount(final MonetaryCurrency currency) {
        this.amountPaid = BigDecimal.ZERO;
        this.amountOutstanding = calculateAmountOutstanding(currency);
        this.paid = false;
    }

    public void resetToOriginal(final MonetaryCurrency currency) {
        this.amountPaid = BigDecimal.ZERO;
        this.amountWaived = BigDecimal.ZERO;
        this.amountWrittenOff = BigDecimal.ZERO;
        this.amountThroughChargePayment = BigDecimal.ZERO;
        this.amountOutstanding = calculateAmountOutstanding(currency);
        this.paid = false;
        this.waived = false;

    }

    public Money getAmountThroughChargePayment(final MonetaryCurrency currency) {
        return Money.of(currency, this.amountThroughChargePayment);
    }

    public Money getUnpaidAmountThroughChargePayment(final MonetaryCurrency currency) {
        return Money.of(currency, this.amountThroughChargePayment).minus(this.amountPaid);
    }

    private void updateAmountThroughChargePayment(final MonetaryCurrency currency) {
        final Money amountThroughChargePayment = getAmountThroughChargePayment(currency);
        if (amountThroughChargePayment.isGreaterThanZero() && amountThroughChargePayment.isGreaterThan(this.getAmount(currency))) {
            this.amountThroughChargePayment = this.getAmount();
        }
    }

    public Money updateWaivedAndAmountPaidThroughChargePaymentAmount(final MonetaryCurrency currency) {
        updateWaivedAmount(currency);
        updateAmountThroughChargePayment(currency);
        return getAmountWaived(currency);
    }

    private void updateWaivedAmount(final MonetaryCurrency currency) {
        final Money waivedAmount = getAmountWaived(currency);
        if (waivedAmount.isGreaterThanZero()) {
            if (waivedAmount.isGreaterThan(this.getAmount(currency))) {
                this.amountWaived = this.getAmount();
                this.amountOutstanding = BigDecimal.ZERO;
                this.paid = false;
                this.waived = true;
            } else if (waivedAmount.isLessThan(this.getAmount(currency))) {
                this.paid = false;
                this.waived = false;
            }
        }
    }

    public void updateInstallment(final LoanRepaymentScheduleInstallment installment) {
        this.installment = installment;
    }

    public Money undoPaidAmountBy(final Money incrementBy, final Money feeAmount) {

        Money amountPaidToDate = Money.of(incrementBy.getCurrency(), this.amountPaid);

        Money amountToDeductOnThisCharge = Money.zero(incrementBy.getCurrency());
        if (incrementBy.isGreaterThanOrEqualTo(amountPaidToDate)) {
            amountToDeductOnThisCharge = amountPaidToDate;
            amountPaidToDate = Money.zero(incrementBy.getCurrency());
            this.amountPaid = amountPaidToDate.getAmount();
            this.amountOutstanding = this.amount;
        } else {
            amountToDeductOnThisCharge = incrementBy;
            amountPaidToDate = amountPaidToDate.minus(incrementBy);
            this.amountPaid = amountPaidToDate.getAmount();
            this.amountOutstanding = calculateAmountOutstanding(incrementBy.getCurrency());
        }
        this.amountThroughChargePayment = feeAmount.getAmount();
        this.paid = determineIfFullyPaid();

        return amountToDeductOnThisCharge;
    }

    public Money undoGlimPaidAmountBy(Money incrementBy, final Money feeAmount) {

        Money amountPaidToDate = Money.of(incrementBy.getCurrency(), this.amountPaid);

        Money amountToDeductOnThisCharge = Money.zero(incrementBy.getCurrency());
        if (incrementBy.isGreaterThanOrEqualTo(amountPaidToDate)) {
            amountToDeductOnThisCharge = amountPaidToDate;
            amountPaidToDate = Money.zero(incrementBy.getCurrency());
            this.amountPaid = amountPaidToDate.getAmount();
            incrementBy = incrementBy.minus(amountToDeductOnThisCharge);
            this.amountOutstanding = calculateAmountOutstanding(incrementBy.getCurrency());

        } else {
            amountToDeductOnThisCharge = incrementBy;
            amountPaidToDate = amountPaidToDate.minus(incrementBy);
            this.amountPaid = amountPaidToDate.getAmount();
            this.amountOutstanding = calculateAmountOutstanding(incrementBy.getCurrency());
        }
        this.amountThroughChargePayment = feeAmount.getAmount();
        this.paid = determineIfFullyPaid();
        if (!MathUtility.isZero(this.amountOutstanding) && isWaived()) {
            this.waived = false;
        }

        return amountToDeductOnThisCharge;
    }

    public LoanCharge getLoancharge() {
        return this.loancharge;
    }

    public LoanRepaymentScheduleInstallment getInstallment() {
        return this.installment;
    }

    public Money waiveGlimLoanCharge(final MonetaryCurrency loanCurrency, final Money chargeAmountToBeWaived) {
        final BigDecimal amount = MathUtility.zeroIfNull(chargeAmountToBeWaived);
        this.amountWaived = MathUtility.isNull(this.amountWaived) ? amount : MathUtility.add(this.amountWaived, amount);
        this.amountOutstanding = MathUtility.subtract(this.amountOutstanding, amount);
        this.paid = false;
        if (MathUtility.isZero(this.amountOutstanding)) {
            this.waived = true;
        }
        return chargeAmountToBeWaived;
    }

    public Money writeOffGlimLoanCharge(final Money writeOffAmount) {
        final BigDecimal amount = MathUtility.zeroIfNull(writeOffAmount);
        this.amountWrittenOff = MathUtility.isNull(this.amountWrittenOff) ? amount : MathUtility.add(this.amountWrittenOff, amount);
        this.amountOutstanding = MathUtility.subtract(this.amountOutstanding, amount);
        return writeOffAmount;
    }

    @Override
    public int compareTo(final LoanInstallmentCharge o) {
        return this.installment.getInstallmentNumber().compareTo(o.installment.getInstallmentNumber());
    }

    public Money writtenOff(final MonetaryCurrency currency) {
        this.amountWrittenOff = this.amountOutstanding;
        this.amountOutstanding = BigDecimal.ZERO;
        this.paid = false;
        this.waived = true;
        return getAmountWrittenOff(currency);
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }
}