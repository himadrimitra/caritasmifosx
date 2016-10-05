package com.finflux.risk.existingloans.data;

import java.math.BigDecimal;
import java.util.Collection;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanStatusEnumData;

public class ExistingLoanData {

    private final Long id;
    private final Long clientId;
    private final Long loanApplicationId;
    private final Long loanId;

    // status
    private final LoanStatusEnumData status;

    // related to
    private final Long sourceCvId;
    private final String sourceCvName;
    private final Long bureauCvId;
    private final String bureauCvName;
    private final Long bureauEnqRefId;
    private final Long lenderCvId;
    private final String lenderCvName;
    private final String lenderNotListed;
    private final Long loanType;
    private final String loanTypeName;
    private final BigDecimal amountBorrowed;
    private final BigDecimal currentOutstanding;
    private final BigDecimal amountOverdue;
    private final BigDecimal writtenOffAmount;
    private final Integer loanTenaure;
    private final EnumOptionData loanTenurePeriodType;
    private final Integer repaymentFrequencyMultipleOf;
    private final EnumOptionData repaymentFrequency;
    private final BigDecimal installmentAmount;
    private final Integer externalLoanPurposeCvId;
    private final String externalLoanPurposeCvName;

    private final Integer gt0Dpd3Mths;
    private final Integer dpd30Mths12;
    private final Integer dpd30Mths24;
    private final Integer dpd60Mths24;

    private final String remark;
    private final Integer archive;

    private final ExistingLoanTimelineData timeline;

    // template
    private final Collection<CodeValueData> sourceCvOptions;
    private final Collection<CodeValueData> bureauCvOptions;
    private final Collection<CodeValueData> lenderCvOptions;
    private final Collection<CodeValueData> loanTypeCvOptions;
    private final Collection<CodeValueData> externalLoanPurposeOptions;
    private final Collection<EnumOptionData> loanTenaureOptions;
    private final Collection<EnumOptionData> termPeriodFrequencyType;
    // added
    private final Collection<LoanStatusEnumData> loanStatusOptions;

    public static ExistingLoanData ExistingLoanDataTemplate(final Collection<CodeValueData> sourceCvOptions,
            final Collection<CodeValueData> bureauCvOptions, final Collection<CodeValueData> lenderCvOptions,
            final Collection<CodeValueData> loanTypeCvOptions, final Collection<CodeValueData> externalLoanPurposeOptions,
            final Collection<EnumOptionData> loanTenaureOptions, final Collection<EnumOptionData> termPeriodFrequencyType,
            final Collection<LoanStatusEnumData> loanStatusOptions) {
        final Long id = null;
        final Long clientId = null;
        final Long loanApplicationId = null;
        final Long loanId = null;
        final LoanStatusEnumData status = null;
        final Long sourceCvId = null;
        final String sourceCvName = null;
        final Long bureauCvId = null;
        final String bureauCvName = null;
        final Long bureauEnqRefId = null;
        final Long lenderCvId = null;
        final String lenderCvName = null;
        final String lenderNotListed = null;
        final Long loanType = null;
        final String loanTypeName = null;
        final BigDecimal amountBorrowed = null;
        final BigDecimal currentOutstanding = null;
        final BigDecimal amountOverdue = null;
        final BigDecimal writtenOffAmount = null;
        final Integer loanTenaure = null;
        final EnumOptionData loanTenurePeriodType = null;
        final Integer repaymentFrequencyMultipleOf = null;
        final EnumOptionData repaymentFrequency = null;
        final BigDecimal installmentAmount = null;
        final Integer externalLoanPurposeCvId = null;
        final String externalLoanPurposeCvName = null;
        final Integer gt0Dpd3Mths = null;
        final Integer dpd30Mths12 = null;
        final Integer dpd30Mths24 = null;
        final Integer dpd60Mths24 = null;
        final String remark = null;
        final Integer archive = null;
        final ExistingLoanTimelineData timeline = null;
        return new ExistingLoanData(id, clientId, loanApplicationId, loanId, status, sourceCvId, sourceCvName, bureauCvId, bureauCvName,
                bureauEnqRefId, lenderCvId, lenderCvName, lenderNotListed, loanType, loanTypeName, amountBorrowed, currentOutstanding,
                amountOverdue, writtenOffAmount, loanTenurePeriodType, loanTenaure, repaymentFrequencyMultipleOf, repaymentFrequency,
                installmentAmount, externalLoanPurposeCvId, externalLoanPurposeCvName, gt0Dpd3Mths, dpd30Mths12, dpd30Mths24, dpd60Mths24,
                remark, archive, timeline, sourceCvOptions, bureauCvOptions, lenderCvOptions, loanTypeCvOptions,
                externalLoanPurposeOptions, loanTenaureOptions, termPeriodFrequencyType, loanStatusOptions);
    }

    public static ExistingLoanData ExistingLoanDataDetails(Long id, Long clientId, Long loanApplicationId, Long loanId,
            LoanStatusEnumData status, Long sourceCvId, String sourceCvName, Long bureauCvId, String bureauCvName, Long bureauEnqRefId,
            Long lenderCvId, String lenderCvName, String lenderNotListed, Long loanType, String loanTypeName, BigDecimal amountBorrowed,
            BigDecimal currentOutstanding, BigDecimal amountOverdue, BigDecimal writtenOffAmount, EnumOptionData loanTenurePeriodType,
            Integer loanTenaure, Integer repaymentFrequencyMultipleOf, EnumOptionData repaymentFrequency, BigDecimal installmentAmount,
            Integer externalLoanPurposeCvId, String externalLoanPurposeCvName, Integer gt0Dpd3Mths, Integer dpd30Mths12,
            Integer dpd30Mths24, Integer dpd60Mths24, String remark, Integer archive, ExistingLoanTimelineData timeline) {
        Collection<CodeValueData> sourceCvOptions = null;
        Collection<CodeValueData> bureauCvOptions = null;
        Collection<CodeValueData> lenderCvOptions = null;
        Collection<CodeValueData> loanTypeCvOptions = null;
        Collection<CodeValueData> externalLoanPurposeOptions = null;
        Collection<EnumOptionData> loanTenaureOptions = null;
        Collection<EnumOptionData> termPeriodFrequencyType = null;
        final Collection<LoanStatusEnumData> loanStatusOptions = null;

        return new ExistingLoanData(id, clientId, loanApplicationId, loanId, status, sourceCvId, sourceCvName, bureauCvId, bureauCvName,
                bureauEnqRefId, lenderCvId, lenderCvName, lenderNotListed, loanType, loanTypeName, amountBorrowed, currentOutstanding,
                amountOverdue, writtenOffAmount, loanTenurePeriodType, loanTenaure, repaymentFrequencyMultipleOf, repaymentFrequency,
                installmentAmount, externalLoanPurposeCvId, externalLoanPurposeCvName, gt0Dpd3Mths, dpd30Mths12, dpd30Mths24, dpd60Mths24,
                remark, archive, timeline, sourceCvOptions, bureauCvOptions, lenderCvOptions, loanTypeCvOptions,
                externalLoanPurposeOptions, loanTenaureOptions, termPeriodFrequencyType, loanStatusOptions);
    }

    public ExistingLoanData(Long id, Long clientId, Long loanApplicationId, Long loanId, LoanStatusEnumData status, Long sourceCvId,
            String sourceCvName, Long bureauCvId, String bureauCvName, Long bureauEnqRefId, Long lenderCvId, String lenderCvName,
            String lenderNotListed, Long loanType, String loanTypeName, BigDecimal amountBorrowed, BigDecimal currentOutstanding,
            BigDecimal amountOverdue, BigDecimal writtenOffAmount, EnumOptionData loanTenurePeriodType, Integer loanTenaure,
            Integer repaymentFrequencyMultipleOf, EnumOptionData repaymentFrequency, BigDecimal installmentAmount,
            Integer externalLoanPurposeCvId, String externalLoanPurposeCvName, Integer gt0Dpd3Mths, Integer dpd30Mths12,
            Integer dpd30Mths24, Integer dpd60Mths24, String remark, Integer archive, ExistingLoanTimelineData timeline,
            Collection<CodeValueData> sourceCvOptions, Collection<CodeValueData> bureauCvOptions,
            Collection<CodeValueData> lenderCvOptions, Collection<CodeValueData> loanTypeCvOptions,
            Collection<CodeValueData> externalLoanPurposeOptions, Collection<EnumOptionData> loanTenaureOptions,
            Collection<EnumOptionData> termPeriodFrequencyType, final Collection<LoanStatusEnumData> loanStatusOptions) {
        this.id = id;
        this.clientId = clientId;
        this.loanApplicationId = loanApplicationId;
        this.loanId = loanId;
        this.status = status;
        this.sourceCvId = sourceCvId;
        this.sourceCvName = sourceCvName;
        this.bureauCvId = bureauCvId;
        this.bureauCvName = bureauCvName;
        this.bureauEnqRefId = bureauEnqRefId;
        this.lenderCvId = lenderCvId;
        this.lenderCvName = lenderCvName;
        this.lenderNotListed = lenderNotListed;
        this.loanType = loanType;
        this.loanTypeName = loanTypeName;
        this.amountBorrowed = amountBorrowed;
        this.currentOutstanding = currentOutstanding;
        this.amountOverdue = amountOverdue;
        this.writtenOffAmount = writtenOffAmount;
        this.loanTenaure = loanTenaure;
        this.loanTenurePeriodType = loanTenurePeriodType;
        this.repaymentFrequencyMultipleOf = repaymentFrequencyMultipleOf;
        this.repaymentFrequency = repaymentFrequency;
        this.installmentAmount = installmentAmount;
        this.externalLoanPurposeCvId = externalLoanPurposeCvId;
        this.externalLoanPurposeCvName = externalLoanPurposeCvName;
        this.gt0Dpd3Mths = gt0Dpd3Mths;
        this.dpd30Mths12 = dpd30Mths12;
        this.dpd30Mths24 = dpd30Mths24;
        this.dpd60Mths24 = dpd60Mths24;
        this.remark = remark;
        this.archive = archive;
        this.timeline = timeline;
        this.sourceCvOptions = sourceCvOptions;
        this.bureauCvOptions = bureauCvOptions;
        this.lenderCvOptions = lenderCvOptions;
        this.loanTypeCvOptions = loanTypeCvOptions;
        this.externalLoanPurposeOptions = externalLoanPurposeOptions;
        this.loanTenaureOptions = loanTenaureOptions;
        this.termPeriodFrequencyType = termPeriodFrequencyType;
        this.loanStatusOptions = loanStatusOptions;
    }
}
