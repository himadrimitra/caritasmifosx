package com.finflux.loanapplicationreference.data;

import java.math.BigDecimal;
import java.util.Collection;

import com.finflux.portfolio.loanemipacks.data.LoanEMIPackData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.joda.time.LocalDate;

public class LoanApplicationSanctionData {

    private final Long loanAppSanctionId;
    private final Long loanApplicationReferenceId;
    private final BigDecimal loanAmountApproved;
    private final LocalDate approvedOnDate;
    private final LocalDate expectedDisbursementDate;
    private final LocalDate repaymentsStartingFromDate;
    private final Integer numberOfRepayments;
    private final EnumOptionData repaymentPeriodFrequency;
    private final Integer repayEvery;
    private final EnumOptionData termPeriodFrequency;
    private final Integer termFrequency;
    private final BigDecimal fixedEmiAmount;
    private final BigDecimal maxOutstandingLoanBalance;
    private final Collection<LoanApplicationSanctionTrancheData> loanApplicationSanctionTrancheDatas;
    private final LoanEMIPackData loanEMIPackData;

    private LoanApplicationSanctionData(final Long loanAppSanctionId, final Long loanApplicationReferenceId,
            final BigDecimal loanAmountApproved, final LocalDate approvedOnDate, LocalDate expectedDisbursementDate,
            final LocalDate repaymentsStartingFromDate, final Integer numberOfRepayments, final EnumOptionData repaymentPeriodFrequency,
            final Integer repayEvery, final EnumOptionData termPeriodFrequency, final Integer termFrequency,
            final BigDecimal fixedEmiAmount, final BigDecimal maxOutstandingLoanBalance,
            final Collection<LoanApplicationSanctionTrancheData> loanApplicationSanctionTrancheDatas,
            final LoanEMIPackData loanEMIPackData) {
        this.loanAppSanctionId = loanAppSanctionId;
        this.loanApplicationReferenceId = loanApplicationReferenceId;
        this.loanAmountApproved = loanAmountApproved;
        this.approvedOnDate = approvedOnDate;
        this.expectedDisbursementDate = expectedDisbursementDate;
        this.repaymentsStartingFromDate = repaymentsStartingFromDate;
        this.numberOfRepayments = numberOfRepayments;
        this.repaymentPeriodFrequency = repaymentPeriodFrequency;
        this.repayEvery = repayEvery;
        this.termPeriodFrequency = termPeriodFrequency;
        this.termFrequency = termFrequency;
        this.fixedEmiAmount = fixedEmiAmount;
        this.maxOutstandingLoanBalance = maxOutstandingLoanBalance;
        this.loanApplicationSanctionTrancheDatas = loanApplicationSanctionTrancheDatas;
        this.loanEMIPackData = loanEMIPackData;
    }

    public static LoanApplicationSanctionData instance(final Long loanAppSanctionId, final Long loanApplicationReferenceId,
            final BigDecimal loanAmountApproved, final LocalDate approvedOnDate, final LocalDate expectedDisbursementDate,
            final LocalDate repaymentsStartingFromDate, final Integer numberOfRepayments, final EnumOptionData repaymentPeriodFrequency,
            final Integer repayEvery, final EnumOptionData termPeriodFrequency, final Integer termFrequency,
            final BigDecimal fixedEmiAmount, final BigDecimal maxOutstandingLoanBalance,
            final Collection<LoanApplicationSanctionTrancheData> loanApplicationSanctionTrancheDatas,
            final LoanEMIPackData loanEMIPackData) {
        return new LoanApplicationSanctionData(loanAppSanctionId, loanApplicationReferenceId, loanAmountApproved, approvedOnDate,
                expectedDisbursementDate, repaymentsStartingFromDate, numberOfRepayments, repaymentPeriodFrequency, repayEvery,
                termPeriodFrequency, termFrequency, fixedEmiAmount, maxOutstandingLoanBalance, loanApplicationSanctionTrancheDatas, loanEMIPackData);
    }

    public Long getLoanAppSanctionId() {
        return this.loanAppSanctionId;
    }

    public Long getLoanApplicationReferenceId() {
        return this.loanApplicationReferenceId;
    }

    public BigDecimal getLoanAmountApproved() {
        return this.loanAmountApproved;
    }

    public LocalDate getApprovedOnDate() {
        return this.approvedOnDate;
    }

    public LocalDate getExpectedDisbursementDate() {
        return this.expectedDisbursementDate;
    }

    public LocalDate getRepaymentsStartingFromDate() {
        return this.repaymentsStartingFromDate;
    }

    public Integer getNumberOfRepayments() {
        return this.numberOfRepayments;
    }

    public EnumOptionData getRepaymentPeriodFrequency() {
        return this.repaymentPeriodFrequency;
    }

    public Integer getRepayEvery() {
        return this.repayEvery;
    }

    public EnumOptionData getTermPeriodFrequency() {
        return this.termPeriodFrequency;
    }

    public Integer getTermFrequency() {
        return this.termFrequency;
    }

    public BigDecimal getFixedEmiAmount() {
        return this.fixedEmiAmount;
    }

    public BigDecimal getMaxOutstandingLoanBalance() {
        return this.maxOutstandingLoanBalance;
    }

    public Collection<LoanApplicationSanctionTrancheData> getLoanApplicationSanctionTrancheDatas() {
        return this.loanApplicationSanctionTrancheDatas;
    }
}
