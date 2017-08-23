package com.finflux.loanapplicationreference.data;

import java.math.BigDecimal;

import com.finflux.portfolio.loanemipacks.data.LoanEMIPackData;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.joda.time.LocalDate;

public class LoanApplicationReferenceData {

    private final Long loanApplicationReferenceId;
    private final String loanApplicationReferenceNo;
    private final String externalIdOne;
    private final String externalIdTwo;
    private final Long loanId;
    private final Long clientId;
    private final String clientName;
    private final Long loanOfficerId;
    private final String loanOfficerName;
    private final Long groupId;
    private final EnumOptionData status;
    private final EnumOptionData accountType;
    private final Long loanProductId;
    private final String loanProductName;
    private final Long loanPurposeId;
    private final CodeValueData loanPurpose;
    private final BigDecimal loanAmountRequested;
    private final Integer numberOfRepayments;
    private final EnumOptionData repaymentPeriodFrequency;
    private final Integer repayEvery;
    private final EnumOptionData termPeriodFrequency;
    private final Integer termFrequency;
    private final BigDecimal fixedEmiAmount;
    private final Integer noOfTranche;
    private final LocalDate submittedOnDate;
    private final PaymentTypeData expectedDisbursalPaymentType; 
    private final PaymentTypeData expectedRepaymentPaymentType;
    private final LoanEMIPackData loanEMIPackData;
    private final Boolean isCoApplicant;

    private LoanApplicationReferenceData(final Long loanApplicationReferenceId, final String loanApplicationReferenceNo,
            final String externalIdOne, final String externalIdTwo, final Long loanId, final Long clientId, final Long loanOfficerId,
            final String loanOfficerName, final Long groupId, final EnumOptionData status, final EnumOptionData accountType,
            final Long loanProductId, final String loanProductName, final Long loanPurposeId, final CodeValueData loanPurpose,
            final BigDecimal loanAmountRequested, final Integer numberOfRepayments, final EnumOptionData repaymentPeriodFrequency,
            final Integer repayEvery, final EnumOptionData termPeriodFrequency, final Integer termFrequency,
            final BigDecimal fixedEmiAmount, final Integer noOfTranche, final LocalDate submittedOnDate,
            final PaymentTypeData expectedDisbursalPaymentType, final PaymentTypeData expectedRepaymentPaymentType,
            final LoanEMIPackData loanEMIPackData, final Boolean isCoApplicant, final String clientName) {
        this.loanApplicationReferenceId = loanApplicationReferenceId;
        this.loanApplicationReferenceNo = loanApplicationReferenceNo;
        this.externalIdOne = externalIdOne;
        this.externalIdTwo = externalIdTwo;
        this.loanId = loanId;
        this.clientId = clientId;
        this.loanOfficerId = loanOfficerId;
        this.loanOfficerName = loanOfficerName;
        this.groupId = groupId;
        this.status = status;
        this.accountType = accountType;
        this.loanProductId = loanProductId;
        this.loanProductName = loanProductName;
        this.loanPurposeId = loanPurposeId;
        this.loanPurpose = loanPurpose;
        this.loanAmountRequested = loanAmountRequested;
        this.numberOfRepayments = numberOfRepayments;
        this.repaymentPeriodFrequency = repaymentPeriodFrequency;
        this.repayEvery = repayEvery;
        this.termPeriodFrequency = termPeriodFrequency;
        this.termFrequency = termFrequency;
        this.fixedEmiAmount = fixedEmiAmount;
        this.noOfTranche = noOfTranche;
        this.submittedOnDate = submittedOnDate;
        this.expectedDisbursalPaymentType = expectedDisbursalPaymentType;
        this.expectedRepaymentPaymentType = expectedRepaymentPaymentType;
        this.loanEMIPackData = loanEMIPackData;
        this.isCoApplicant = isCoApplicant;
        this.clientName = clientName;

    }

    public static LoanApplicationReferenceData instance(final Long loanApplicationReferenceId, final String loanApplicationReferenceNo,
            final String externalIdOne, final String externalIdTwo, final Long loanId, final Long clientId, final Long loanOfficerId,
            final String loanOfficerName, final Long groupId, final EnumOptionData status, final EnumOptionData accountType,
            final Long loanProductId, final String loanProductName, final Long loanPurposeId, final CodeValueData loanPurpose,
            final BigDecimal loanAmountRequested, final Integer numberOfRepayments, final EnumOptionData repaymentPeriodFrequency,
            final Integer repayEvery, final EnumOptionData termPeriodFrequency, final Integer termFrequency,
            final BigDecimal fixedEmiAmount, final Integer noOfTranche, final LocalDate submittedOnDate,
            final PaymentTypeData expectedDisbursalPaymentType, final PaymentTypeData expectedRepaymentPaymentType,
            final LoanEMIPackData loanEMIPackData, final Boolean isCoApplicant, final String clientName) {

        return new LoanApplicationReferenceData(loanApplicationReferenceId, loanApplicationReferenceNo, externalIdOne, externalIdTwo,
                loanId, clientId, loanOfficerId, loanOfficerName, groupId, status, accountType, loanProductId, loanProductName,
                loanPurposeId, loanPurpose, loanAmountRequested, numberOfRepayments, repaymentPeriodFrequency, repayEvery,
                termPeriodFrequency, termFrequency, fixedEmiAmount, noOfTranche, submittedOnDate, expectedDisbursalPaymentType,
                expectedRepaymentPaymentType, loanEMIPackData, isCoApplicant, clientName);
    }
    
    public static LoanApplicationReferenceData forLookUp(final Long loanApplicationReferenceId, final String loanApplicationReferenceNo,
            final String externalIdOne, final Long loanId, final EnumOptionData accountType, final EnumOptionData status,
            final Long loanProductId, final String loanProductName, final BigDecimal loanAmountRequested, final Boolean isCoApplicant) {
        final String externalIdTwo = null;
        final Long clientId = null;
        final Long loanOfficerId = null;
        final String loanOfficerName = null;
        final Long groupId = null;
        final Long loanPurposeId = null;
        final CodeValueData loanPurpose = null;
        final Integer numberOfRepayments = null;
        final EnumOptionData repaymentPeriodFrequency = null;
        final Integer repayEvery = null;
        final EnumOptionData termPeriodFrequency = null;
        final Integer termFrequency = null;
        final BigDecimal fixedEmiAmount = null;
        final Integer noOfTranche = null;
        final LocalDate submittedOnDate = null;
        final PaymentTypeData expectedDisbursalPaymentType = null;
        final PaymentTypeData expectedRepaymentPaymentType = null;
        final LoanEMIPackData loanEMIPackData = null;
        final String clientName = null;

        return new LoanApplicationReferenceData(loanApplicationReferenceId, loanApplicationReferenceNo, externalIdOne, externalIdTwo,
                loanId, clientId, loanOfficerId, loanOfficerName, groupId, status, accountType, loanProductId, loanProductName,
                loanPurposeId, loanPurpose, loanAmountRequested, numberOfRepayments, repaymentPeriodFrequency, repayEvery,
                termPeriodFrequency, termFrequency, fixedEmiAmount, noOfTranche, submittedOnDate, expectedDisbursalPaymentType,
                expectedRepaymentPaymentType, loanEMIPackData, isCoApplicant, clientName);
    }

    public Long getLoanApplicationReferenceId() {
        return this.loanApplicationReferenceId;
    }

    public String getLoanApplicationReferenceNo() {
        return this.loanApplicationReferenceNo;
    }

    public String getExternalIdOne() {
        return this.externalIdOne;
    }

    public String getExternalIdTwo() {
        return this.externalIdTwo;
    }

    public Long getLoanId() {
        return this.loanId;
    }

    public Long getClientId() {
        return this.clientId;
    }

    public Long getLoanOfficerId() {
        return this.loanOfficerId;
    }

    public String getLoanOfficerName() {
        return this.loanOfficerName;
    }

    public Long getGroupId() {
        return this.groupId;
    }

    public EnumOptionData getStatus() {
        return this.status;
    }

    public EnumOptionData getAccountType() {
        return this.accountType;
    }

    public Long getLoanProductId() {
        return this.loanProductId;
    }

    public String getLoanProductName() {
        return this.loanProductName;
    }

    public Long getLoanPurposeId() {
        return this.loanPurposeId;
    }

    public CodeValueData getLoanPurpose() {
        return this.loanPurpose;
    }

    public BigDecimal getLoanAmountRequested() {
        return this.loanAmountRequested;
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

    public Integer getNoOfTranche() {
        return this.noOfTranche;
    }

    public LocalDate getSubmittedOnDate() {
        return this.submittedOnDate;
    }

}
