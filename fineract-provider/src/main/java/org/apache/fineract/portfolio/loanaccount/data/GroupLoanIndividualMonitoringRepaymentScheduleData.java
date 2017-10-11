/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;

public class GroupLoanIndividualMonitoringRepaymentScheduleData {

    private final Integer period;
    private final LocalDate fromDate;
    private final LocalDate dueDate;
    private final LocalDate obligationsMetOnDate;
    private final Boolean complete;
    private final Integer daysInPeriod;
    private final BigDecimal principalDisbursed;
    private final BigDecimal principalOriginalDue;
    private final BigDecimal principalDue;
    private final BigDecimal principalPaid;
    private final BigDecimal principalWrittenOff;
    private final BigDecimal principalOutstanding;
    private final BigDecimal principalLoanBalanceOutstanding;
    @SuppressWarnings("unused")
    private final BigDecimal interestOriginalDue;
    private final BigDecimal interestDue;
    private final BigDecimal interestPaid;
    private final BigDecimal interestWaived;
    private final BigDecimal interestWrittenOff;
    private final BigDecimal interestOutstanding;
    private final BigDecimal feeChargesDue;
    private final BigDecimal feeChargesPaid;
    private final BigDecimal feeChargesWaived;
    private final BigDecimal feeChargesWrittenOff;
    private final BigDecimal feeChargesOutstanding;
    private final BigDecimal penaltyChargesDue;
    private final BigDecimal penaltyChargesPaid;
    private final BigDecimal penaltyChargesWaived;
    private final BigDecimal penaltyChargesWrittenOff;
    private final BigDecimal penaltyChargesOutstanding;

    @SuppressWarnings("unused")
    private final BigDecimal totalOriginalDueForPeriod;
    private final BigDecimal totalDueForPeriod;
    private final BigDecimal totalPaidForPeriod;
    private final BigDecimal totalPaidInAdvanceForPeriod;
    private final BigDecimal totalPaidLateForPeriod;
    private final BigDecimal totalWaivedForPeriod;
    private final BigDecimal totalWrittenOffForPeriod;
    private final BigDecimal totalOutstandingForPeriod;
    private final BigDecimal totalOverdue;
    private final BigDecimal totalActualCostOfLoanForPeriod;
    private final BigDecimal totalInstallmentAmountForPeriod;
    private List<GroupLoanIndividualMonitoringRepaymentScheduleData> periodsWithDisbursement = new ArrayList<>();

    public GroupLoanIndividualMonitoringRepaymentScheduleData(final Integer periodNumber, final LocalDate fromDate,
            final LocalDate dueDate, final LocalDate obligationsMetOnDate, final boolean complete, final BigDecimal principalOriginalDue,
            final BigDecimal principalPaid, final BigDecimal principalWrittenOff, final BigDecimal principalOutstanding,
            final BigDecimal principalLoanBalanceOutstanding, final BigDecimal interestDueOnPrincipalOutstanding,
            final BigDecimal interestPaid, final BigDecimal interestWaived, final BigDecimal interestWrittenOff,
            final BigDecimal interestOutstanding, final BigDecimal feeChargesDue, final BigDecimal feeChargesPaid,
            final BigDecimal feeChargesWaived, final BigDecimal feeChargesWrittenOff, final BigDecimal feeChargesOutstanding,
            final BigDecimal penaltyChargesDue, final BigDecimal penaltyChargesPaid, final BigDecimal penaltyChargesWaived,
            final BigDecimal penaltyChargesWrittenOff, final BigDecimal penaltyChargesOutstanding, final BigDecimal totalDueForPeriod,
            final BigDecimal totalPaid, final BigDecimal totalPaidInAdvanceForPeriod, final BigDecimal totalPaidLateForPeriod,
            final BigDecimal totalWaived, final BigDecimal totalWrittenOff, final BigDecimal totalOutstanding,
            final BigDecimal totalActualCostOfLoanForPeriod, final BigDecimal totalInstallmentAmountForPeriod) {

        this.period = periodNumber;
        this.fromDate = fromDate;
        this.dueDate = dueDate;
        this.obligationsMetOnDate = obligationsMetOnDate;
        this.complete = complete;
        if (fromDate != null) {
            this.daysInPeriod = Days.daysBetween(this.fromDate, this.dueDate).getDays();
        } else {
            this.daysInPeriod = null;
        }
        this.principalDisbursed = null;
        this.principalOriginalDue = principalOriginalDue;
        this.principalDue = principalOriginalDue;
        this.principalPaid = principalPaid;
        this.principalWrittenOff = principalWrittenOff;
        this.principalOutstanding = principalOutstanding;
        this.principalLoanBalanceOutstanding = principalLoanBalanceOutstanding;

        this.interestOriginalDue = interestDueOnPrincipalOutstanding;
        this.interestDue = interestDueOnPrincipalOutstanding;
        this.interestPaid = interestPaid;
        this.interestWaived = interestWaived;
        this.interestWrittenOff = interestWrittenOff;
        this.interestOutstanding = interestOutstanding;

        this.feeChargesDue = feeChargesDue;
        this.feeChargesPaid = feeChargesPaid;
        this.feeChargesWaived = feeChargesWaived;
        this.feeChargesWrittenOff = feeChargesWrittenOff;
        this.feeChargesOutstanding = feeChargesOutstanding;

        this.penaltyChargesDue = penaltyChargesDue;
        this.penaltyChargesPaid = penaltyChargesPaid;
        this.penaltyChargesWaived = penaltyChargesWaived;
        this.penaltyChargesWrittenOff = penaltyChargesWrittenOff;
        this.penaltyChargesOutstanding = penaltyChargesOutstanding;

        this.totalOriginalDueForPeriod = totalDueForPeriod;
        this.totalDueForPeriod = totalDueForPeriod;
        this.totalPaidForPeriod = totalPaid;
        this.totalPaidInAdvanceForPeriod = totalPaidInAdvanceForPeriod;
        this.totalPaidLateForPeriod = totalPaidLateForPeriod;
        this.totalWaivedForPeriod = totalWaived;
        this.totalWrittenOffForPeriod = totalWrittenOff;
        this.totalOutstandingForPeriod = totalOutstanding;
        this.totalActualCostOfLoanForPeriod = totalActualCostOfLoanForPeriod;
        this.totalInstallmentAmountForPeriod = totalInstallmentAmountForPeriod;
        this.periodsWithDisbursement = null;

        if (dueDate.isBefore(DateUtils.getLocalDateOfTenant())) {
            this.totalOverdue = this.totalOutstandingForPeriod;
        } else {
            this.totalOverdue = null;
        }
    }

    public static GroupLoanIndividualMonitoringRepaymentScheduleData repaymentPeriods(Integer installmentNumber, LocalDate dueDate,
            LocalDate fromDate, BigDecimal principalAmount, BigDecimal interestAmount, BigDecimal feeChargesAmount,
            BigDecimal penaltyChargesAmount, BigDecimal totalDueForPeriod, BigDecimal principalLoanBalanceOutstanding) {

        final LocalDate obligationsMetOnDate = null;
        final boolean complete = false;
        final BigDecimal principalPaid = null;
        final BigDecimal principalWrittenOff = null;
        final BigDecimal principalOutstanding = null;
        final BigDecimal interestPaid = null;
        final BigDecimal interestWaived = null;
        final BigDecimal interestWrittenOff = null;
        final BigDecimal interestOutstanding = null;
        final BigDecimal feeChargesPaid = null;
        final BigDecimal feeChargesWaived = null;
        final BigDecimal feeChargesWrittenOff = null;
        final BigDecimal feeChargesOutstanding = null;
        final BigDecimal penaltyChargesPaid = null;
        final BigDecimal penaltyChargesWaived = null;
        final BigDecimal penaltyChargesWrittenOff = null;
        final BigDecimal penaltyChargesOutstanding = null;
        final BigDecimal totalPaid = null;
        final BigDecimal totalPaidInAdvanceForPeriod = null;
        final BigDecimal totalPaidLateForPeriod = null;
        final BigDecimal totalWaived = null;
        final BigDecimal totalWrittenOff = null;
        final BigDecimal totalOutstanding = null;
        final BigDecimal totalActualCostOfLoanForPeriod = null;
        final BigDecimal totalInstallmentAmountForPeriod = null;

        return new GroupLoanIndividualMonitoringRepaymentScheduleData(installmentNumber, fromDate, dueDate, obligationsMetOnDate, complete,
                principalAmount, principalPaid, principalWrittenOff, principalOutstanding, principalLoanBalanceOutstanding, interestAmount,
                interestPaid, interestWaived, interestWrittenOff, interestOutstanding, feeChargesAmount, feeChargesPaid, feeChargesWaived,
                feeChargesWrittenOff, feeChargesOutstanding, penaltyChargesAmount, penaltyChargesPaid, penaltyChargesWaived,
                penaltyChargesWrittenOff, penaltyChargesOutstanding, totalDueForPeriod, totalPaid, totalPaidInAdvanceForPeriod,
                totalPaidLateForPeriod, totalWaived, totalWrittenOff, totalOutstanding, totalActualCostOfLoanForPeriod,
                totalInstallmentAmountForPeriod);
    }

    public static GroupLoanIndividualMonitoringRepaymentScheduleData disbursementOnlyPeriod(LocalDate disbursementDate, BigDecimal amount,
            BigDecimal totalFeeChargesDueAtDisbursement, boolean disbursed) {
        final Integer periodNumber = null;
        final LocalDate from = null;

        return new GroupLoanIndividualMonitoringRepaymentScheduleData(periodNumber, from, disbursementDate, amount,
                totalFeeChargesDueAtDisbursement, disbursed);
    }

    private GroupLoanIndividualMonitoringRepaymentScheduleData(final Integer periodNumber, final LocalDate fromDate,
            final LocalDate dueDate, final BigDecimal principalDisbursed, final BigDecimal chargesDueAtTimeOfDisbursement,
            final boolean isDisbursed) {
        this.period = periodNumber;
        this.fromDate = fromDate;
        this.dueDate = dueDate;
        this.obligationsMetOnDate = null;
        this.complete = null;
        if (fromDate != null) {
            this.daysInPeriod = Days.daysBetween(this.fromDate, this.dueDate).getDays();
        } else {
            this.daysInPeriod = null;
        }
        this.principalDisbursed = principalDisbursed;
        this.principalOriginalDue = null;
        this.principalDue = null;
        this.principalPaid = null;
        this.principalWrittenOff = null;
        this.principalOutstanding = null;
        this.principalLoanBalanceOutstanding = principalDisbursed;

        this.interestOriginalDue = null;
        this.interestDue = null;
        this.interestPaid = null;
        this.interestWaived = null;
        this.interestWrittenOff = null;
        this.interestOutstanding = null;

        this.feeChargesDue = chargesDueAtTimeOfDisbursement;
        if (isDisbursed) {
            this.feeChargesPaid = chargesDueAtTimeOfDisbursement;
            this.feeChargesWaived = null;
            this.feeChargesWrittenOff = null;
            this.feeChargesOutstanding = null;
        } else {
            this.feeChargesPaid = null;
            this.feeChargesWaived = null;
            this.feeChargesWrittenOff = null;
            this.feeChargesOutstanding = chargesDueAtTimeOfDisbursement;
        }

        this.penaltyChargesDue = null;
        this.penaltyChargesPaid = null;
        this.penaltyChargesWaived = null;
        this.penaltyChargesWrittenOff = null;
        this.penaltyChargesOutstanding = null;

        this.totalOriginalDueForPeriod = chargesDueAtTimeOfDisbursement;
        this.totalDueForPeriod = chargesDueAtTimeOfDisbursement;
        this.totalPaidForPeriod = this.feeChargesPaid;
        this.totalPaidInAdvanceForPeriod = null;
        this.totalPaidLateForPeriod = null;
        this.totalWaivedForPeriod = null;
        this.totalWrittenOffForPeriod = null;
        this.totalOutstandingForPeriod = this.feeChargesOutstanding;
        this.totalActualCostOfLoanForPeriod = this.feeChargesDue;
        this.totalInstallmentAmountForPeriod = null;
        this.periodsWithDisbursement = null;
        if (dueDate.isBefore(DateUtils.getLocalDateOfTenant())) {
            this.totalOverdue = this.totalOutstandingForPeriod;
        } else {
            this.totalOverdue = null;
        }
    }

    public GroupLoanIndividualMonitoringRepaymentScheduleData(Collection<GroupLoanIndividualMonitoringRepaymentScheduleData> periods) {
        this.period = null;
        this.fromDate = null;
        this.dueDate = null;
        this.obligationsMetOnDate = null;
        this.complete = null;
        this.daysInPeriod = null;
        this.principalDisbursed = null;
        this.principalOriginalDue = null;
        this.principalDue = null;
        this.principalPaid = null;
        this.principalWrittenOff = null;
        this.principalOutstanding = null;
        this.principalLoanBalanceOutstanding = null;

        this.interestOriginalDue = null;
        this.interestDue = null;
        this.interestPaid = null;
        this.interestWaived = null;
        this.interestWrittenOff = null;
        this.interestOutstanding = null;

        this.feeChargesDue = null;
        this.feeChargesPaid = null;
        this.feeChargesWaived = null;
        this.feeChargesWrittenOff = null;
        this.feeChargesOutstanding = null;

        this.penaltyChargesDue = null;
        this.penaltyChargesPaid = null;
        this.penaltyChargesWaived = null;
        this.penaltyChargesWrittenOff = null;
        this.penaltyChargesOutstanding = null;

        this.totalOriginalDueForPeriod = null;
        this.totalDueForPeriod = null;
        this.totalPaidForPeriod = this.feeChargesPaid;
        this.totalPaidInAdvanceForPeriod = null;
        this.totalPaidLateForPeriod = null;
        this.totalWaivedForPeriod = null;
        this.totalWrittenOffForPeriod = null;
        this.totalOutstandingForPeriod = null;
        this.totalActualCostOfLoanForPeriod = null;
        this.totalInstallmentAmountForPeriod = null;
        this.periodsWithDisbursement.addAll(periods);
        this.totalOverdue = null;
    }

    public static GroupLoanIndividualMonitoringRepaymentScheduleData GlimRepaymentScheduleData(
            Collection<GroupLoanIndividualMonitoringRepaymentScheduleData> periods) {
        return new GroupLoanIndividualMonitoringRepaymentScheduleData(periods);
    }

}
