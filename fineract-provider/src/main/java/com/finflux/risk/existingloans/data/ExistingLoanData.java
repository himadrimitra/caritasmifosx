package com.finflux.risk.existingloans.data;

import java.math.BigDecimal;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanStatusEnumData;
import org.joda.time.LocalDate;

import com.finflux.risk.creditbureau.configuration.data.CreditBureauProductData;

public class ExistingLoanData {

    private final Long id;
    private final Long clientId;
    private final Long loanApplicationId;
    private final Long loanId;
    private final CodeValueData source;
    private final CreditBureauProductData creditBureauProductData;
    private final Long loanCreditBureauEnquiryId;
    private final CodeValueData lender;
    private final String lenderName;
    private final CodeValueData loanType;
    private final BigDecimal amountBorrowed;
    private final BigDecimal currentOutstanding;
    private final BigDecimal amtOverdue;
    private final BigDecimal writtenOffAmount;
    private final Integer loanTenure;
    private final EnumOptionData loanTenurePeriodType;
    private final EnumOptionData repaymentFrequency;
    private final Integer repaymentFrequencyMultipleOf;
    private final BigDecimal installmentAmount;
    private final CodeValueData externalLoanPurpose;
    private final LoanStatusEnumData loanStatus;
    private final LocalDate disbursedDate;
    private final LocalDate maturityDate;
    private final LocalDate closedDate;
    private final Integer gt0Dpd3Mths;
    private final Integer dpd30Mths12;
    private final Integer dpd30Mths24;
    private final Integer dpd60Mths24;
    private final String remark;
    private final Integer archive;

    private ExistingLoanData(final Long id, final Long clientId, final Long loanApplicationId, final Long loanId,
            final CodeValueData source, final CreditBureauProductData creditBureauProductData, final Long loanCreditBureauEnquiryId,
            final CodeValueData lender, final String lenderName, final CodeValueData loanType, final BigDecimal amountBorrowed,
            final BigDecimal currentOutstanding, final BigDecimal amtOverdue, final BigDecimal writtenOffAmount, final Integer loanTenure,
            final EnumOptionData loanTenurePeriodType, final EnumOptionData repaymentFrequency, final Integer repaymentFrequencyMultipleOf,
            final BigDecimal installmentAmount, final CodeValueData externalLoanPurpose, final LoanStatusEnumData loanStatus,
            final LocalDate disbursedDate, final LocalDate maturityDate, final LocalDate closedDate, final Integer gt0Dpd3Mths,
            final Integer dpd30Mths12, final Integer dpd30Mths24, final Integer dpd60Mths24, final String remark, final Integer archive) {
        this.id = id;
        this.clientId = clientId;
        this.loanApplicationId = loanApplicationId;
        this.loanId = loanId;
        this.source = source;
        this.creditBureauProductData = creditBureauProductData;
        this.loanCreditBureauEnquiryId = loanCreditBureauEnquiryId;
        this.lender = lender;
        this.lenderName = lenderName;
        this.loanType = loanType;
        this.amountBorrowed = amountBorrowed;
        this.currentOutstanding = currentOutstanding;
        this.amtOverdue = amtOverdue;
        this.writtenOffAmount = writtenOffAmount;
        this.loanTenure = loanTenure;
        this.loanTenurePeriodType = loanTenurePeriodType;
        this.repaymentFrequency = repaymentFrequency;
        this.repaymentFrequencyMultipleOf = repaymentFrequencyMultipleOf;
        this.installmentAmount = installmentAmount;
        this.externalLoanPurpose = externalLoanPurpose;
        this.loanStatus = loanStatus;
        this.disbursedDate = disbursedDate;
        this.maturityDate = maturityDate;
        this.closedDate = closedDate;
        this.gt0Dpd3Mths = gt0Dpd3Mths;
        this.dpd30Mths12 = dpd30Mths12;
        this.dpd30Mths24 = dpd30Mths24;
        this.dpd60Mths24 = dpd60Mths24;
        this.remark = remark;
        this.archive = archive;
    }

    public static ExistingLoanData instance(final Long id, final Long clientId, final Long loanApplicationId, final Long loanId,
            final CodeValueData source, final CreditBureauProductData creditBureauProductData, final Long loanCreditBureauEnquiryId,
            final CodeValueData lender, final String lenderName, final CodeValueData loanType, final BigDecimal amountBorrowed,
            final BigDecimal currentOutstanding, final BigDecimal amtOverdue, final BigDecimal writtenOffAmount, final Integer loanTenure,
            final EnumOptionData loanTenurePeriodType, final EnumOptionData repaymentFrequency, final Integer repaymentFrequencyMultipleOf,
            final BigDecimal installmentAmount, final CodeValueData externalLoanPurpose, final LoanStatusEnumData loanStatus,
            final LocalDate disbursedDate, final LocalDate maturityDate, final LocalDate closedDate, final Integer gt0Dpd3Mths,
            final Integer dpd30Mths12, final Integer dpd30Mths24, final Integer dpd60Mths24, final String remark, final Integer archive) {
        return new ExistingLoanData(id, clientId, loanApplicationId, loanId, source, creditBureauProductData, loanCreditBureauEnquiryId,
                lender, lenderName, loanType, amountBorrowed, currentOutstanding, amtOverdue, writtenOffAmount, loanTenure,
                loanTenurePeriodType, repaymentFrequency, repaymentFrequencyMultipleOf, installmentAmount, externalLoanPurpose, loanStatus,
                disbursedDate, maturityDate, closedDate, gt0Dpd3Mths, dpd30Mths12, dpd30Mths24, dpd60Mths24, remark, archive);
    }

    public Long getId() {
        return this.id;
    }

    public Long getClientId() {
        return this.clientId;
    }

    public Long getLoanApplicationId() {
        return this.loanApplicationId;
    }

    public Long getLoanId() {
        return this.loanId;
    }

    public CodeValueData getSource() {
        return this.source;
    }

    public CreditBureauProductData getCreditBureauProductData() {
        return this.creditBureauProductData;
    }

    public Long getLoanCreditBureauEnquiryId() {
        return this.loanCreditBureauEnquiryId;
    }

    public CodeValueData getLender() {
        return this.lender;
    }

    public String getLenderName() {
        return this.lenderName;
    }

    public CodeValueData getLoanType() {
        return this.loanType;
    }

    public BigDecimal getAmountBorrowed() {
        return this.amountBorrowed;
    }

    public BigDecimal getCurrentOutstanding() {
        return this.currentOutstanding;
    }

    public BigDecimal getAmtOverdue() {
        return this.amtOverdue;
    }

    public BigDecimal getWrittenOffAmount() {
        return this.writtenOffAmount;
    }

    public Integer getLoanTenure() {
        return this.loanTenure;
    }

    public EnumOptionData getLoanTenurePeriodType() {
        return this.loanTenurePeriodType;
    }

    public EnumOptionData getRepaymentFrequency() {
        return this.repaymentFrequency;
    }

    public Integer getRepaymentFrequencyMultipleOf() {
        return this.repaymentFrequencyMultipleOf;
    }

    public BigDecimal getInstallmentAmount() {
        return this.installmentAmount;
    }

    public CodeValueData getExternalLoanPurpose() {
        return this.externalLoanPurpose;
    }

    public LoanStatusEnumData getLoanStatus() {
        return this.loanStatus;
    }

    public LocalDate getDisbursedDate() {
        return this.disbursedDate;
    }

    public LocalDate getMaturityDate() {
        return this.maturityDate;
    }

    public Integer getGt0Dpd3Mths() {
        return this.gt0Dpd3Mths;
    }

    public Integer getDpd30Mths12() {
        return this.dpd30Mths12;
    }

    public Integer getDpd30Mths24() {
        return this.dpd30Mths24;
    }

    public Integer getDpd60Mths24() {
        return this.dpd60Mths24;
    }

    public String getRemark() {
        return this.remark;
    }

    public Integer getArchive() {
        return this.archive;
    }

    public LocalDate getClosedDate() {
        return this.closedDate;
    }
}