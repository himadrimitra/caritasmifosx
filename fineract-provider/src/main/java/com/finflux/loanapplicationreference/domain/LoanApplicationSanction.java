package com.finflux.loanapplicationreference.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.*;

import com.finflux.portfolio.loanemipacks.domain.LoanEMIPack;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;

import com.finflux.loanapplicationreference.api.LoanApplicationReferenceApiConstants;

@Entity
@Table(name = "f_loan_application_sanction")
public class LoanApplicationSanction extends AbstractAuditableCustom<AppUser, Long> {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_app_ref_id")
    private LoanApplicationReference loanApplicationReference;

    @Column(name = "loan_amount_approved", scale = 6, precision = 19, nullable = false)
    private BigDecimal loanAmountApproved;

    @Temporal(TemporalType.DATE)
    @Column(name = "approvedon_date")
    private Date approvedOnDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "expected_disbursement_date")
    private Date expectedDisbursementDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "repayments_starting_from_date")
    private Date repaymentsStartingFromDate;

    @Column(name = "number_of_repayments", nullable = false)
    private Integer numberOfRepayments;

    @Column(name = "repayment_period_frequency_enum", nullable = true)
    private Integer repaymentPeriodFrequencyEnum;

    @Column(name = "repay_every", nullable = true)
    private Integer repayEvery;

    @Column(name = "term_period_frequency_enum", nullable = true)
    private Integer termPeriodFrequencyEnum;

    @Column(name = "term_frequency", nullable = true)
    private Integer termFrequency;

    @Column(name = "fixed_emi_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal fixedEmiAmount;

    @Column(name = "max_outstanding_loan_balance", scale = 6, precision = 19, nullable = true)
    private BigDecimal maxOutstandingLoanBalance;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loanApplicationSanction", orphanRemoval = true)
    private List<LoanApplicationSanctionTranche> loanApplicationSanctionTranches = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "loan_emi_pack_id", nullable = true)
    private LoanEMIPack loanEMIPack;

    protected LoanApplicationSanction() {}

    LoanApplicationSanction(final LoanApplicationReference loanApplicationReference, final BigDecimal loanAmountApproved,
            final Date approvedOnDate, final Date expectedDisbursementDate, final Date repaymentsStartingFromDate,
            final Integer numberOfRepayments, final Integer repaymentPeriodFrequencyEnum, final Integer repayEvery,
            final Integer termPeriodFrequencyEnum, final Integer termFrequency, final BigDecimal fixedEmiAmount,
            final BigDecimal maxOutstandingLoanBalance, final LoanEMIPack loanEMIPack) {
        this.loanApplicationReference = loanApplicationReference;
        this.loanAmountApproved = loanAmountApproved;
        this.approvedOnDate = approvedOnDate;
        this.expectedDisbursementDate = expectedDisbursementDate;
        this.repaymentsStartingFromDate = repaymentsStartingFromDate;
        this.numberOfRepayments = numberOfRepayments;
        this.repaymentPeriodFrequencyEnum = repaymentPeriodFrequencyEnum;
        this.repayEvery = repayEvery;
        this.termPeriodFrequencyEnum = termPeriodFrequencyEnum;
        this.termFrequency = termFrequency;
        this.fixedEmiAmount = fixedEmiAmount;
        this.maxOutstandingLoanBalance = maxOutstandingLoanBalance;
        this.loanEMIPack = loanEMIPack;
    }

    public static LoanApplicationSanction create(final LoanApplicationReference loanApplicationReference,
            final BigDecimal loanAmountApproved, final Date approvedOnDate, final Date expectedDisbursementDate,
            final Date repaymentsStartingFromDate, final Integer numberOfRepayments, final Integer repaymentPeriodFrequencyEnum,
            final Integer repayEvery, final Integer termPeriodFrequencyEnum, final Integer termFrequency, final BigDecimal fixedEmiAmount,
            final BigDecimal maxOutstandingLoanBalance, final LoanEMIPack loanEMIPack) {
        return new LoanApplicationSanction(loanApplicationReference, loanAmountApproved, approvedOnDate, expectedDisbursementDate,
                repaymentsStartingFromDate, numberOfRepayments, repaymentPeriodFrequencyEnum, repayEvery, termPeriodFrequencyEnum,
                termFrequency, fixedEmiAmount, maxOutstandingLoanBalance, loanEMIPack);
    }

    public void updateLoanApplicationSanctionTranches(final List<LoanApplicationSanctionTranche> loanApplicationSanctionTranches) {
        this.loanApplicationSanctionTranches.clear();
        this.loanApplicationSanctionTranches.addAll(loanApplicationSanctionTranches);
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(10);

        if (command.isChangeInBigDecimalParameterNamed(LoanApplicationReferenceApiConstants.loanAmountApprovedParamName,
                this.loanAmountApproved)) {
            final BigDecimal newValue = command
                    .bigDecimalValueOfParameterNamed(LoanApplicationReferenceApiConstants.loanAmountApprovedParamName);
            actualChanges.put(LoanApplicationReferenceApiConstants.loanAmountApprovedParamName, newValue);
            this.loanAmountApproved = newValue;
        }

        final String dateFormatAsInput = command.dateFormat();
        final String localeAsInput = command.locale();

        if (command.isChangeInLocalDateParameterNamed(LoanApplicationReferenceApiConstants.approvedOnDateParaName,
                approvedOnDateParamNameLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(LoanApplicationReferenceApiConstants.approvedOnDateParaName);
            actualChanges.put(LoanApplicationReferenceApiConstants.approvedOnDateParaName, valueAsInput);
            actualChanges.put(LoanApplicationReferenceApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(LoanApplicationReferenceApiConstants.localeParamName, localeAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(LoanApplicationReferenceApiConstants.approvedOnDateParaName);
            this.approvedOnDate = newValue.toDate();
        }

        if (command.isChangeInLocalDateParameterNamed(LoanApplicationReferenceApiConstants.expectedDisbursementDateParaName,
                expectedDisbursementDateParamNameLocalDate())) {
            final String valueAsInput = command
                    .stringValueOfParameterNamed(LoanApplicationReferenceApiConstants.expectedDisbursementDateParaName);
            actualChanges.put(LoanApplicationReferenceApiConstants.expectedDisbursementDateParaName, valueAsInput);
            actualChanges.put(LoanApplicationReferenceApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(LoanApplicationReferenceApiConstants.localeParamName, localeAsInput);

            final LocalDate newValue = command
                    .localDateValueOfParameterNamed(LoanApplicationReferenceApiConstants.expectedDisbursementDateParaName);
            this.expectedDisbursementDate = newValue.toDate();
        }

        if (command.parameterExists(LoanApplicationReferenceApiConstants.repaymentsStartingFromDateParaName)
                && command.isChangeInLocalDateParameterNamed(LoanApplicationReferenceApiConstants.repaymentsStartingFromDateParaName,
                        repaymentsStartingFromDateParaNameLocalDate())) {
            final String valueAsInput = command
                    .stringValueOfParameterNamed(LoanApplicationReferenceApiConstants.repaymentsStartingFromDateParaName);
            actualChanges.put(LoanApplicationReferenceApiConstants.repaymentsStartingFromDateParaName, valueAsInput);
            actualChanges.put(LoanApplicationReferenceApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(LoanApplicationReferenceApiConstants.localeParamName, localeAsInput);

            final LocalDate newValue = command
                    .localDateValueOfParameterNamed(LoanApplicationReferenceApiConstants.repaymentsStartingFromDateParaName);
            this.repaymentsStartingFromDate = newValue.toDate();
        }

        if (this.loanEMIPack != null) {
            if (command.parameterExists(LoanApplicationReferenceApiConstants.loanEMIPackIdParamName)
                    && command.isChangeInLongParameterNamed(LoanApplicationReferenceApiConstants.loanEMIPackIdParamName, this.loanEMIPack.getId())) {
                final Long newValue = command.longValueOfParameterNamed(LoanApplicationReferenceApiConstants.loanEMIPackIdParamName);
                actualChanges.put(LoanApplicationReferenceApiConstants.loanEMIPackIdParamName, newValue);
            }
        } else {
            if (command.parameterExists(LoanApplicationReferenceApiConstants.loanEMIPackIdParamName)) {
                final Long newValue = command.longValueOfParameterNamed(LoanApplicationReferenceApiConstants.loanEMIPackIdParamName);
                actualChanges.put(LoanApplicationReferenceApiConstants.loanEMIPackIdParamName, newValue);
            }
        }

        if (command.isChangeInIntegerParameterNamed(LoanApplicationReferenceApiConstants.numberOfRepaymentsParamName,
                this.numberOfRepayments)) {
            final Integer newValue = command.integerValueOfParameterNamed(LoanApplicationReferenceApiConstants.numberOfRepaymentsParamName);
            actualChanges.put(LoanApplicationReferenceApiConstants.numberOfRepaymentsParamName, newValue);
            this.numberOfRepayments = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(LoanApplicationReferenceApiConstants.repaymentPeriodFrequencyEnumParamName,
                this.repaymentPeriodFrequencyEnum)) {
            final Integer newValue = command
                    .integerValueOfParameterNamed(LoanApplicationReferenceApiConstants.repaymentPeriodFrequencyEnumParamName);
            actualChanges.put(LoanApplicationReferenceApiConstants.repaymentPeriodFrequencyEnumParamName, newValue);
            this.repaymentPeriodFrequencyEnum = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(LoanApplicationReferenceApiConstants.repayEveryParamName, this.repayEvery)) {
            final Integer newValue = command.integerValueOfParameterNamed(LoanApplicationReferenceApiConstants.repayEveryParamName);
            actualChanges.put(LoanApplicationReferenceApiConstants.repayEveryParamName, newValue);
            this.repayEvery = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(LoanApplicationReferenceApiConstants.termPeriodFrequencyEnumParamName,
                this.termPeriodFrequencyEnum)) {
            final Integer newValue = command
                    .integerValueOfParameterNamed(LoanApplicationReferenceApiConstants.termPeriodFrequencyEnumParamName);
            actualChanges.put(LoanApplicationReferenceApiConstants.termPeriodFrequencyEnumParamName, newValue);
            this.termPeriodFrequencyEnum = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(LoanApplicationReferenceApiConstants.termFrequencyParamName, this.termFrequency)) {
            final Integer newValue = command.integerValueOfParameterNamed(LoanApplicationReferenceApiConstants.termFrequencyParamName);
            actualChanges.put(LoanApplicationReferenceApiConstants.termFrequencyParamName, newValue);
            this.termFrequency = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(LoanApplicationReferenceApiConstants.fixedEmiAmountParamName, this.fixedEmiAmount)) {
            final BigDecimal newValue = command
                    .bigDecimalValueOfParameterNamed(LoanApplicationReferenceApiConstants.fixedEmiAmountParamName);
            actualChanges.put(LoanApplicationReferenceApiConstants.fixedEmiAmountParamName, newValue);
            this.fixedEmiAmount = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(LoanApplicationReferenceApiConstants.maxOutstandingLoanBalanceParamName,
                this.maxOutstandingLoanBalance)) {
            final BigDecimal newValue = command
                    .bigDecimalValueOfParameterNamed(LoanApplicationReferenceApiConstants.maxOutstandingLoanBalanceParamName);
            actualChanges.put(LoanApplicationReferenceApiConstants.maxOutstandingLoanBalanceParamName, newValue);
            this.maxOutstandingLoanBalance = newValue;
        }

        return actualChanges;
    }

    public LocalDate approvedOnDateParamNameLocalDate() {
        LocalDate approvedOnDate = null;
        if (this.approvedOnDate != null) {
            approvedOnDate = LocalDate.fromDateFields(this.approvedOnDate);
        }
        return approvedOnDate;
    }

    public LocalDate expectedDisbursementDateParamNameLocalDate() {
        LocalDate expectedDisbursementDate = null;
        if (this.expectedDisbursementDate != null) {
            expectedDisbursementDate = LocalDate.fromDateFields(this.expectedDisbursementDate);
        }
        return expectedDisbursementDate;
    }

    public LocalDate repaymentsStartingFromDateParaNameLocalDate() {
        LocalDate repaymentsStartingFromDate = null;
        if (this.repaymentsStartingFromDate != null) {
            repaymentsStartingFromDate = LocalDate.fromDateFields(this.repaymentsStartingFromDate);
        }
        return repaymentsStartingFromDate;
    }

    public BigDecimal getLoanAmountApproved() {
        return this.loanAmountApproved;
    }
    public void setLoanAmountApproved(BigDecimal loanAmountApproved) {
        this.loanAmountApproved = loanAmountApproved;
    }

    public void setNumberOfRepayments(Integer numberOfRepayments) {
        this.numberOfRepayments = numberOfRepayments;
    }

    public void setRepaymentPeriodFrequencyEnum(Integer repaymentPeriodFrequencyEnum) {
        this.repaymentPeriodFrequencyEnum = repaymentPeriodFrequencyEnum;
    }

    public void setRepayEvery(Integer repayEvery) {
        this.repayEvery = repayEvery;
    }

    public void setTermPeriodFrequencyEnum(Integer termPeriodFrequencyEnum) {
        this.termPeriodFrequencyEnum = termPeriodFrequencyEnum;
    }

    public void setTermFrequency(Integer termFrequency) {
        this.termFrequency = termFrequency;
    }

    public void setFixedEmiAmount(BigDecimal fixedEmiAmount) {
        this.fixedEmiAmount = fixedEmiAmount;
    }

    public void setLoanEMIPack(LoanEMIPack loanEMIPack) {
        this.loanEMIPack = loanEMIPack;
    }

}
