package com.finflux.loanapplicationreference.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentType;
import org.apache.fineract.useradministration.domain.AppUser;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;

import com.finflux.loanapplicationreference.api.LoanApplicationReferenceApiConstants;
import com.finflux.portfolio.loan.purpose.domain.LoanPurpose;

@Entity
@Table(name = "f_loan_application_reference")
public class LoanApplicationReference extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "loan_application_reference_no", nullable = false)
    private String loanApplicationReferenceNo;

    @Column(name = "external_id_one", nullable = true)
    private String externalIdOne;

    @Column(name = "external_id_two", nullable = true)
    private String externalIdTwo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = true)
    private Loan loan;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = true)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "loan_officer_id", nullable = true)
    private Staff loanOfficer;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = true)
    private Group group;

    @Column(name = "status_enum", nullable = false)
    private Integer statusEnum;

    @Column(name = "account_type_enum", nullable = true)
    private Integer accountTypeEnum;

    @ManyToOne
    @JoinColumn(name = "loan_product_id", nullable = false)
    private LoanProduct loanProduct;

    @ManyToOne
    @JoinColumn(name = "loan_purpose_id", nullable = true)
    private LoanPurpose loanPurpose;

    @Column(name = "loan_amount_requested", scale = 6, precision = 19, nullable = false)
    private BigDecimal loanAmountRequested;

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

    @Column(name = "no_of_tranche", nullable = false)
    private Integer noOfTranche;

    @Temporal(TemporalType.DATE)
    @Column(name = "submittedon_date")
    private Date submittedOnDate;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "loanApplicationReference", orphanRemoval = true, fetch = FetchType.LAZY)
    private LoanApplicationSanction loanApplicationSanction;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loanApplicationReference", orphanRemoval = true)
    private List<LoanApplicationCharge> loanApplicationCharges = new ArrayList<>();
    
    @ManyToOne(optional = true,fetch=FetchType.LAZY)
    @JoinColumn(name = "expected_disbursal_payment_type_id", nullable = true)
    private PaymentType expectedDisbursalPaymentType;
       
    @ManyToOne(optional = true,fetch=FetchType.LAZY)
    @JoinColumn(name = "expected_repayment_payment_type_id", nullable = true)
    private PaymentType expectedRepaymentPaymentType;

    protected LoanApplicationReference() {}

    LoanApplicationReference(final String loanApplicationReferenceNo, final String externalIdOne, final String externalIdTwo,
            final Client client, final Staff loanOfficer, final Group group, final Integer statusEnum, final Integer accountTypeEnum,
            final LoanProduct loanProduct, final LoanPurpose loanPurpose, final BigDecimal loanAmountRequested,
            final Integer numberOfRepayments, final Integer repaymentPeriodFrequencyEnum, final Integer repayEvery,
            final Integer termPeriodFrequencyEnum, final Integer termFrequency, final BigDecimal fixedEmiAmount, final Integer noOfTranche,
            final Date submittedOnDate, final PaymentType expectedDisbursalPaymentType, final PaymentType expectedRepaymentPaymentType) {
        this.loanApplicationReferenceNo = loanApplicationReferenceNo;
        this.externalIdOne = externalIdOne;
        this.externalIdTwo = externalIdTwo;
        this.client = client;
        this.loanOfficer = loanOfficer;
        this.group = group;
        this.statusEnum = statusEnum;
        this.accountTypeEnum = accountTypeEnum;
        this.loanProduct = loanProduct;
        this.loanPurpose = loanPurpose;
        this.loanAmountRequested = loanAmountRequested;
        this.numberOfRepayments = numberOfRepayments;
        this.repaymentPeriodFrequencyEnum = repaymentPeriodFrequencyEnum;
        this.repayEvery = repayEvery;
        this.termPeriodFrequencyEnum = termPeriodFrequencyEnum;
        this.termFrequency = termFrequency;
        this.fixedEmiAmount = fixedEmiAmount;
        this.noOfTranche = noOfTranche;
        this.submittedOnDate = submittedOnDate;
        this.expectedDisbursalPaymentType= expectedDisbursalPaymentType;
        this.expectedRepaymentPaymentType = expectedRepaymentPaymentType;
    }

    public static LoanApplicationReference create(final String loanApplicationReferenceNo, final String externalIdOne,
            final String externalIdTwo, final Client client, final Staff loanOfficer, final Group group, final Integer statusEnum,
            final Integer accountTypeEnum, final LoanProduct loanProduct, final LoanPurpose loanPurpose,
            final BigDecimal loanAmountRequested, final Integer numberOfRepayments, final Integer repaymentPeriodFrequencyEnum,
            final Integer repayEvery, final Integer termPeriodFrequencyEnum, final Integer termFrequency, final BigDecimal fixedEmiAmount,
            final Integer noOfTranche, final Date submittedOnDate,final PaymentType expectedDisbursalPaymentType,
            final PaymentType expectedRepaymentPaymentType ) {

        return new LoanApplicationReference(loanApplicationReferenceNo, externalIdOne, externalIdTwo, client, loanOfficer, group,
                statusEnum, accountTypeEnum, loanProduct, loanPurpose, loanAmountRequested, numberOfRepayments,
                repaymentPeriodFrequencyEnum, repayEvery, termPeriodFrequencyEnum, termFrequency, fixedEmiAmount, noOfTranche,
                submittedOnDate, expectedDisbursalPaymentType, expectedRepaymentPaymentType);
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(10);

        if (command.parameterExists(LoanApplicationReferenceApiConstants.externalIdOneParamName)) {
            if (command.isChangeInStringParameterNamed(LoanApplicationReferenceApiConstants.externalIdOneParamName, this.externalIdOne)) {
                final String newValue = command.stringValueOfParameterNamed(LoanApplicationReferenceApiConstants.externalIdOneParamName);
                actualChanges.put(LoanApplicationReferenceApiConstants.externalIdOneParamName, newValue);
                this.externalIdOne = newValue;
            }
        }

        if (command.parameterExists(LoanApplicationReferenceApiConstants.externalIdTwoParamName)) {
            if (command.isChangeInStringParameterNamed(LoanApplicationReferenceApiConstants.externalIdTwoParamName, this.externalIdTwo)) {
                final String newValue = command.stringValueOfParameterNamed(LoanApplicationReferenceApiConstants.externalIdTwoParamName);
                actualChanges.put(LoanApplicationReferenceApiConstants.externalIdTwoParamName, newValue);
                this.externalIdTwo = newValue;
            }
        }

        if (this.client != null) {
            if (command.parameterExists(LoanApplicationReferenceApiConstants.clientIdParamName)
                    && command.isChangeInLongParameterNamed(LoanApplicationReferenceApiConstants.clientIdParamName, this.client.getId())) {
                final Long newValue = command.longValueOfParameterNamed(LoanApplicationReferenceApiConstants.clientIdParamName);
                actualChanges.put(LoanApplicationReferenceApiConstants.clientIdParamName, newValue);
            }
        } else {
            if (command.parameterExists(LoanApplicationReferenceApiConstants.clientIdParamName)) {
                final Long newValue = command.longValueOfParameterNamed(LoanApplicationReferenceApiConstants.clientIdParamName);
                actualChanges.put(LoanApplicationReferenceApiConstants.clientIdParamName, newValue);
            }
        }

        if (this.loanOfficer != null) {
            if (command.parameterExists(LoanApplicationReferenceApiConstants.loanOfficerIdParamName)
                    && command.isChangeInLongParameterNamed(LoanApplicationReferenceApiConstants.loanOfficerIdParamName,
                            this.loanOfficer.getId())) {
                final Long newValue = command.longValueOfParameterNamed(LoanApplicationReferenceApiConstants.loanOfficerIdParamName);
                actualChanges.put(LoanApplicationReferenceApiConstants.loanOfficerIdParamName, newValue);
            }
        } else {
            if (command.parameterExists(LoanApplicationReferenceApiConstants.loanOfficerIdParamName)) {
                final Long newValue = command.longValueOfParameterNamed(LoanApplicationReferenceApiConstants.loanOfficerIdParamName);
                actualChanges.put(LoanApplicationReferenceApiConstants.loanOfficerIdParamName, newValue);
            }
        }

        if (this.loanProduct != null) {
            if (command.parameterExists(LoanApplicationReferenceApiConstants.loanProductIdParamName)
                    && command.isChangeInLongParameterNamed(LoanApplicationReferenceApiConstants.loanProductIdParamName,
                            this.loanProduct.getId())) {
                final Long newValue = command.longValueOfParameterNamed(LoanApplicationReferenceApiConstants.loanProductIdParamName);
                actualChanges.put(LoanApplicationReferenceApiConstants.loanProductIdParamName, newValue);
            }
        } else {
            if (command.parameterExists(LoanApplicationReferenceApiConstants.loanProductIdParamName)) {
                final Long newValue = command.longValueOfParameterNamed(LoanApplicationReferenceApiConstants.loanProductIdParamName);
                actualChanges.put(LoanApplicationReferenceApiConstants.loanProductIdParamName, newValue);
            }
        }

        if (this.loanPurpose != null) {
            if (command.parameterExists(LoanApplicationReferenceApiConstants.loanPurposeIdParamName)
                    && command.isChangeInLongParameterNamed(LoanApplicationReferenceApiConstants.loanPurposeIdParamName,
                            this.loanPurpose.getId())) {
                final Long newValue = command.longValueOfParameterNamed(LoanApplicationReferenceApiConstants.loanPurposeIdParamName);
                actualChanges.put(LoanApplicationReferenceApiConstants.loanPurposeIdParamName, newValue);
            }
        } else {
            if (command.parameterExists(LoanApplicationReferenceApiConstants.loanPurposeIdParamName)) {
                final Long newValue = command.longValueOfParameterNamed(LoanApplicationReferenceApiConstants.loanPurposeIdParamName);
                actualChanges.put(LoanApplicationReferenceApiConstants.loanPurposeIdParamName, newValue);
            }
        }

        if (command.isChangeInBigDecimalParameterNamed(LoanApplicationReferenceApiConstants.loanAmountRequestedParamName,
                this.loanAmountRequested)) {
            final BigDecimal newValue = command
                    .bigDecimalValueOfParameterNamed(LoanApplicationReferenceApiConstants.loanAmountRequestedParamName);
            actualChanges.put(LoanApplicationReferenceApiConstants.loanAmountRequestedParamName, newValue);
            this.loanAmountRequested = newValue;
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

        if (command.isChangeInIntegerParameterNamed(LoanApplicationReferenceApiConstants.noOfTrancheParamName, this.noOfTranche)) {
            final Integer newValue = command.integerValueOfParameterNamed(LoanApplicationReferenceApiConstants.noOfTrancheParamName);
            actualChanges.put(LoanApplicationReferenceApiConstants.noOfTrancheParamName, newValue);
            this.noOfTranche = newValue;
        }

        Long expectedDisbursalTypeId = null;
        if (this.getExpectedDisbursalPaymentType() != null) {
            expectedDisbursalTypeId = this.getExpectedDisbursalPaymentType().getId();
        }
        if (command.isChangeInLongParameterNamed(LoanApplicationReferenceApiConstants.expectedDisbursalPaymentTypeParamName,
                expectedDisbursalTypeId)) {
            actualChanges.put(LoanApplicationReferenceApiConstants.expectedDisbursalPaymentTypeParamName,
                    command.integerValueOfParameterNamed(LoanApplicationReferenceApiConstants.expectedDisbursalPaymentTypeParamName));
        }

        Long expectedRepaymentPaymentTypeId = null;
        if (this.getExpectedRepaymentPaymentType() != null) {
            expectedRepaymentPaymentTypeId = this.getExpectedRepaymentPaymentType().getId();
        }

        if (command.isChangeInLongParameterNamed(LoanApplicationReferenceApiConstants.expectedRepaymentPaymentTypeParamName,
                expectedRepaymentPaymentTypeId)) {
            actualChanges.put(LoanApplicationReferenceApiConstants.expectedRepaymentPaymentTypeParamName,
                    command.integerValueOfParameterNamed(LoanApplicationReferenceApiConstants.expectedRepaymentPaymentTypeParamName));
        }

        final String dateFormatAsInput = command.dateFormat();
        final String localeAsInput = command.locale();
        if (command.isChangeInLocalDateParameterNamed(LoanApplicationReferenceApiConstants.submittedOnDateParamName,
                submittedOnDateParamNameLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(LoanApplicationReferenceApiConstants.submittedOnDateParamName);
            actualChanges.put(LoanApplicationReferenceApiConstants.submittedOnDateParamName, valueAsInput);
            actualChanges.put(LoanApplicationReferenceApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(LoanApplicationReferenceApiConstants.localeParamName, localeAsInput);

            final LocalDate newValue = command
                    .localDateValueOfParameterNamed(LoanApplicationReferenceApiConstants.submittedOnDateParamName);
            this.submittedOnDate = newValue.toDate();
        }

        return actualChanges;
    }

    public LocalDate submittedOnDateParamNameLocalDate() {
        LocalDate submittedOnDate = null;
        if (this.submittedOnDate != null) {
            submittedOnDate = LocalDate.fromDateFields(this.submittedOnDate);
        }
        return submittedOnDate;
    }

    public void updateClient(final Client client) {
        this.client = client;
    }

    public void updateLoanOfficer(final Staff loanOfficer) {
        this.loanOfficer = loanOfficer;
    }

    public void updateLoanProduct(final LoanProduct loanProduct) {
        this.loanProduct = loanProduct;
    }

    public void updateLoanPurpose(final LoanPurpose loanPurpose) {
        this.loanPurpose = loanPurpose;
    }

    public void updateStatusEnum(final Integer statusEnum) {
        this.statusEnum = statusEnum;
    }

    public LoanProduct getLoanProduct() {
        return this.loanProduct;
    }

    public void updateLoanApplicationSanction(final LoanApplicationSanction loanApplicationSanction) {
        this.loanApplicationSanction = loanApplicationSanction;
    }

    public void updateLoanApplicationCharges(final List<LoanApplicationCharge> loanApplicationCharges) {
        this.loanApplicationCharges.clear();
        if (loanApplicationCharges != null) {
            this.loanApplicationCharges.addAll(loanApplicationCharges);
        }
    }

    public Integer getNoOfTranche() {
        return this.noOfTranche;
    }

    public LoanApplicationSanction getLoanApplicationSanction() {
        return this.loanApplicationSanction;
    }

    public Integer getStatusEnum() {
        return this.statusEnum;
    }

    public Client getClient() {
        return this.client;
    }

    public void updateLoan(final Loan loan) {
        this.loan = loan;
    }

    public BigDecimal getLoanAmountRequested() {
        return this.loanAmountRequested;
    }

    public PaymentType getExpectedDisbursalPaymentType() {
        return this.expectedDisbursalPaymentType;
    }

    public void setExpectedDisbursalPaymentType(PaymentType expectedDisbursalPaymentType) {
        this.expectedDisbursalPaymentType = expectedDisbursalPaymentType;
    }

    public PaymentType getExpectedRepaymentPaymentType() {
        return this.expectedRepaymentPaymentType;
    }

    public void setExpectedRepaymentPaymentType(PaymentType expectedRepaymentPaymentType) {
        this.expectedRepaymentPaymentType = expectedRepaymentPaymentType;
    }

    public Loan getLoan() {
        return this.loan;
    }

    public String getLoanApplicationReferenceNo() {
        return this.loanApplicationReferenceNo;
    }
}