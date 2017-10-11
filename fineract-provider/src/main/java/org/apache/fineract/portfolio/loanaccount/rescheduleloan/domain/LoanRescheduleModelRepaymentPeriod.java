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
package org.apache.fineract.portfolio.loanaccount.rescheduleloan.domain;

import java.math.BigDecimal;

import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanSchedulePeriodData;
import org.joda.time.LocalDate;

public final class LoanRescheduleModelRepaymentPeriod implements LoanRescheduleModalPeriod {

    private int periodNumber;
    private int oldPeriodNumber;
    private LocalDate fromDate;
    private LocalDate dueDate;
    private Money principalDue;
    private Money outstandingLoanBalance;
    private Money interestDue;
    private Money feeChargesDue;
    private Money penaltyChargesDue;
    private Money totalDue;
    private boolean isNew;

    public LoanRescheduleModelRepaymentPeriod(final int periodNumber, final int oldPeriodNumber, final LocalDate fromDate,
            final LocalDate dueDate, final Money principalDue, final Money outstandingLoanBalance, final Money interestDue,
            final Money feeChargesDue, final Money penaltyChargesDue, final Money totalDue, final boolean isNew) {
        this.periodNumber = periodNumber;
        this.oldPeriodNumber = oldPeriodNumber;
        this.fromDate = fromDate;
        this.dueDate = dueDate;
        this.principalDue = principalDue;
        this.outstandingLoanBalance = outstandingLoanBalance;
        this.interestDue = interestDue;
        this.feeChargesDue = feeChargesDue;
        this.penaltyChargesDue = penaltyChargesDue;
        this.totalDue = totalDue;
        this.isNew = isNew;
    }

    public static LoanRescheduleModelRepaymentPeriod instance(final int periodNumber, final int oldPeriodNumber, final LocalDate fromDate,
            final LocalDate dueDate, final Money principalDue, final Money outstandingLoanBalance, final Money interestDue,
            final Money feeChargesDue, final Money penaltyChargesDue, final Money totalDue, final boolean isNew) {

        return new LoanRescheduleModelRepaymentPeriod(periodNumber, oldPeriodNumber, fromDate, dueDate, principalDue,
                outstandingLoanBalance, interestDue, feeChargesDue, penaltyChargesDue, totalDue, isNew);
    }

    @Override
    public LoanSchedulePeriodData toData() {
        return LoanSchedulePeriodData.repaymentOnlyPeriod(this.periodNumber, this.fromDate, this.dueDate, this.principalDue.getAmount(),
                this.outstandingLoanBalance.getAmount(), this.interestDue.getAmount(), this.feeChargesDue.getAmount(),
                this.penaltyChargesDue.getAmount(), this.totalDue.getAmount(), this.principalDue.plus(this.interestDue).getAmount(), false);
    }

    @Override
    public Integer periodNumber() {
        return this.periodNumber;
    }

    @Override
    public Integer oldPeriodNumber() {
        return this.oldPeriodNumber;
    }

    @Override
    public LocalDate periodFromDate() {
        return this.fromDate;
    }

    @Override
    public LocalDate periodDueDate() {
        return this.dueDate;
    }

    @Override
    public BigDecimal principalDue() {
        BigDecimal value = null;

        if (this.principalDue != null) {
            value = this.principalDue.getAmount();
        }

        return value;
    }

    @Override
    public BigDecimal interestDue() {
        BigDecimal value = null;

        if (this.interestDue != null) {
            value = this.interestDue.getAmount();
        }

        return value;
    }

    @Override
    public BigDecimal feeChargesDue() {
        BigDecimal value = null;

        if (this.feeChargesDue != null) {
            value = this.feeChargesDue.getAmount();
        }

        return value;
    }

    @Override
    public BigDecimal penaltyChargesDue() {
        BigDecimal value = null;

        if (this.penaltyChargesDue != null) {
            value = this.penaltyChargesDue.getAmount();
        }

        return value;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    public void updatePeriodNumber(final Integer periodNumber) {
        this.periodNumber = periodNumber;
    }

    public void updateOldPeriodNumber(final Integer oldPeriodNumber) {
        this.oldPeriodNumber = oldPeriodNumber;
    }

    public void updatePeriodFromDate(final LocalDate periodFromDate) {
        this.fromDate = periodFromDate;
    }

    public void updatePeriodDueDate(final LocalDate periodDueDate) {
        this.dueDate = periodDueDate;
    }

    public void updatePrincipalDue(final Money principalDue) {
        this.principalDue = principalDue;
    }

    public void updateInterestDue(final Money interestDue) {
        this.interestDue = interestDue;
    }

    public void updateFeeChargesDue(final Money feeChargesDue) {
        this.feeChargesDue = feeChargesDue;
    }

    public void updatePenaltyChargesDue(final Money penaltyChargesDue) {
        this.penaltyChargesDue = penaltyChargesDue;
    }

    public void updateOutstandingLoanBalance(final Money outstandingLoanBalance) {
        this.outstandingLoanBalance = outstandingLoanBalance;
    }

    public void updateTotalDue(final Money totalDue) {
        this.totalDue = totalDue;
    }

    public void updateIsNew(final boolean isNew) {
        this.isNew = isNew;
    }
}
