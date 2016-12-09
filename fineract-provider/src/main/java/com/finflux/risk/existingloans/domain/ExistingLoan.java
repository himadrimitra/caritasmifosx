package com.finflux.risk.existingloans.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;

import com.finflux.risk.creditbureau.configuration.domain.CreditBureauProduct;
import com.finflux.risk.existingloans.api.ExistingLoanApiConstants;

@SuppressWarnings({ "serial" })
@Entity
@Table(name = "f_existing_loan")
public class ExistingLoan extends AbstractAuditableCustom<AppUser, Long> {

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "loan_application_id", nullable = true)
    private Long loanApplicationId;

    @Column(name = "loan_id", nullable = true)
    private Long loanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = true)
    private CodeValue source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creditbureau_product_id", nullable = true)
    private CreditBureauProduct creditBureauProduct;

    @Column(name = "loan_creditbureau_enquiry_id", nullable = true)
    private Long loanCreditBureauEnquiryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lender_cv_id", nullable = true)
    private CodeValue lender;

    @Column(name = "lender_name", nullable = true)
    private String lenderName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loantype_cv_id", nullable = true)
    private CodeValue loanType;

    @Column(name = "amount_borrowed", scale = 6, precision = 19, nullable = true)
    private BigDecimal amountBorrowed;

    @Column(name = "current_outstanding", scale = 6, precision = 19, nullable = true)
    private BigDecimal currentOutstanding;

    @Column(name = "amt_overdue", scale = 6, precision = 19, nullable = true)
    private BigDecimal amtOverdue;

    @Column(name = "written_off_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal writtenOffAmount;

    @Column(name = "loan_tenure", nullable = true)
    private Integer loanTenure;

    @Column(name = "loan_tenure_period_type", nullable = true)
    private Integer loanTenurePeriodType;

    @Column(name = "repayment_frequency", nullable = true)
    private Integer repaymentFrequency;

    @Column(name = "repayment_frequency_multiple_of", nullable = true)
    private Integer repaymentFrequencyMultipleOf;

    @Column(name = "installment_amount", nullable = true)
    private BigDecimal installmentAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "external_loan_purpose_cv_id", nullable = true)
    private CodeValue externalLoanPurpose;

    @Column(name = "loan_status_id", nullable = true)
    private Integer loanStatusId;

    @Temporal(TemporalType.DATE)
    @Column(name = "disbursed_date")
    private Date disbursedDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "maturity_date")
    private Date maturityDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "closed_date")
    private Date closedDate;

    @Column(name = "gt_0_dpd_3_mths")
    private Integer gt0dpd3mths;

    @Column(name = "30_dpd_12_mths")
    private Integer dpd30mths12;

    @Column(name = "30_dpd_24_mths")
    private Integer dpd30mths24;

    @Column(name = "60_dpd_24_mths")
    private Integer dpd60mths24;

    @Column(name = "remark", nullable = true)
    private String remark;

    @Column(name = "archive")
    private Integer archive;

    public static ExistingLoan saveExistingLoan(final Client client, final Long loanApplicationId, final Long loanId,
            final CodeValue source, final CreditBureauProduct creditBureauProduct, final Long loanCreditBureauEnquiryId,
            final CodeValue lender, final String lenderName, final CodeValue loanType, final BigDecimal amountBorrowed,
            final BigDecimal currentOutstanding, final BigDecimal amtOverdue, final BigDecimal writtenoffamount, final Integer loanTenure,
            final Integer loanTenurePeriodType, final Integer repaymentFrequency, final Integer repaymentFrequencyMultipleOf,
            final BigDecimal installmentAmount, final CodeValue externalLoanPurpose, final Integer status, final LocalDate disbursedDate,
            final LocalDate maturityDate, final LocalDate closedDate, final Integer gt0dpd3mths, final Integer dpd30mths12,
            final Integer dpd30mths24, final Integer dpd60mths24, final String remark, final Integer archive) {
        return new ExistingLoan(client, loanId, loanApplicationId, source, creditBureauProduct, loanCreditBureauEnquiryId, lender,
                lenderName, loanType, amountBorrowed, currentOutstanding, amtOverdue, writtenoffamount, loanTenure, loanTenurePeriodType,
                repaymentFrequency, repaymentFrequencyMultipleOf, installmentAmount, externalLoanPurpose, status, disbursedDate,
                maturityDate, closedDate, gt0dpd3mths, dpd30mths12, dpd30mths24, dpd60mths24, remark, archive);
    }

    public Map<String, Object> update(final JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>(9);
        final String dateFormatAsInput = command.dateFormat();
        final String localeAsInput = command.locale();

        if (command.isChangeInLongParameterNamed(ExistingLoanApiConstants.loanApplicationIdParamName, this.loanApplicationId)) {
            final Long newValue = command.longValueOfParameterNamed(ExistingLoanApiConstants.loanApplicationIdParamName);
            actualChanges.put(ExistingLoanApiConstants.loanApplicationIdParamName, newValue);
            this.loanApplicationId = newValue;
        }

        if (command.isChangeInLongParameterNamed(ExistingLoanApiConstants.loanIdParamName, this.loanId)) {
            final Long newValue = command.longValueOfParameterNamed(ExistingLoanApiConstants.loanIdParamName);
            actualChanges.put(ExistingLoanApiConstants.loanIdParamName, newValue);
            this.loanId = newValue;
        }

        if (command.isChangeInLongParameterNamed(ExistingLoanApiConstants.sourceIdParamName, sourceId())) {
            final Long newValue = command.longValueOfParameterNamed(ExistingLoanApiConstants.sourceIdParamName);
            actualChanges.put(ExistingLoanApiConstants.sourceIdParamName, newValue);
        }

        if (command.isChangeInLongParameterNamed(ExistingLoanApiConstants.creditBureauProductIdParamName, creditBureauProductId())) {
            final Long newValue = command.longValueOfParameterNamed(ExistingLoanApiConstants.creditBureauProductIdParamName);
            actualChanges.put(ExistingLoanApiConstants.creditBureauProductIdParamName, newValue);
        }

        if (command.isChangeInLongParameterNamed(ExistingLoanApiConstants.loanCreditBureauEnquiryIdParamName,
                this.loanCreditBureauEnquiryId)) {
            final Long newValue = command.longValueOfParameterNamed(ExistingLoanApiConstants.loanCreditBureauEnquiryIdParamName);
            actualChanges.put(ExistingLoanApiConstants.loanCreditBureauEnquiryIdParamName, newValue);
            this.loanCreditBureauEnquiryId = newValue;
        }

        if (command.isChangeInLongParameterNamed(ExistingLoanApiConstants.lenderIdParamName, lenderId())) {
            final Long newValue = command.longValueOfParameterNamed(ExistingLoanApiConstants.lenderIdParamName);
            actualChanges.put(ExistingLoanApiConstants.lenderIdParamName, newValue);
        }

        if (command.isChangeInStringParameterNamed(ExistingLoanApiConstants.lenderNameParamName, this.lenderName)) {
            final String newValue = command.stringValueOfParameterNamed(ExistingLoanApiConstants.lenderNameParamName);
            actualChanges.put(ExistingLoanApiConstants.lenderNameParamName, newValue);
            this.lenderName = newValue;
        }

        if (command.isChangeInLongParameterNamed(ExistingLoanApiConstants.loanTypeIdParamName, loanTypeId())) {
            final Long newValue = command.longValueOfParameterNamed(ExistingLoanApiConstants.loanTypeIdParamName);
            actualChanges.put(ExistingLoanApiConstants.loanTypeIdParamName, newValue);
        }

        if (command.isChangeInBigDecimalParameterNamed(ExistingLoanApiConstants.amountBorrowedParamName, this.amountBorrowed)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ExistingLoanApiConstants.amountBorrowedParamName);
            actualChanges.put(ExistingLoanApiConstants.amountBorrowedParamName, newValue);
            this.amountBorrowed = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ExistingLoanApiConstants.currentOutstandingIdParamName, this.currentOutstanding)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ExistingLoanApiConstants.currentOutstandingIdParamName);
            actualChanges.put(ExistingLoanApiConstants.currentOutstandingIdParamName, newValue);
            this.currentOutstanding = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ExistingLoanApiConstants.amtOverdueParamName, this.amtOverdue)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ExistingLoanApiConstants.amtOverdueParamName);
            actualChanges.put(ExistingLoanApiConstants.amtOverdueParamName, newValue);
            this.amtOverdue = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ExistingLoanApiConstants.writtenoffamountParamName, this.writtenOffAmount)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ExistingLoanApiConstants.writtenoffamountParamName);
            actualChanges.put(ExistingLoanApiConstants.writtenoffamountParamName, newValue);
            this.writtenOffAmount = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(ExistingLoanApiConstants.loanTenureParamName, this.loanTenure)) {
            final Integer newValue = command.integerValueOfParameterNamed(ExistingLoanApiConstants.loanTenureParamName);
            actualChanges.put(ExistingLoanApiConstants.loanTenureParamName, newValue);
            this.loanTenure = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(ExistingLoanApiConstants.loanTenurePeriodTypeParamName, this.loanTenurePeriodType)) {
            final Integer newValue = command.integerValueOfParameterNamed(ExistingLoanApiConstants.loanTenurePeriodTypeParamName);
            final PeriodFrequencyType tenure = PeriodFrequencyType.fromInt(newValue);
            actualChanges.put(ExistingLoanApiConstants.loanTenurePeriodTypeParamName, tenure.getValue());
            this.loanTenurePeriodType = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(ExistingLoanApiConstants.repaymentFrequencyParamName, this.repaymentFrequency)) {
            final Integer newValue = command.integerValueOfParameterNamed(ExistingLoanApiConstants.repaymentFrequencyParamName);
            final PeriodFrequencyType newTermPeriodFrequencyType = PeriodFrequencyType.fromInt(newValue);
            actualChanges.put(ExistingLoanApiConstants.repaymentFrequencyParamName, newTermPeriodFrequencyType.getValue());
            this.repaymentFrequency = newValue;
        }
        if (command.isChangeInIntegerParameterNamed(ExistingLoanApiConstants.repaymentFrequencyMultipleOfParamName,
                this.repaymentFrequencyMultipleOf)) {
            final Integer newValue = command.integerValueOfParameterNamed(ExistingLoanApiConstants.repaymentFrequencyMultipleOfParamName);
            actualChanges.put(ExistingLoanApiConstants.repaymentFrequencyMultipleOfParamName, newValue);
            this.repaymentFrequencyMultipleOf = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(ExistingLoanApiConstants.installmentAmountParamName, this.installmentAmount)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ExistingLoanApiConstants.installmentAmountParamName);
            actualChanges.put(ExistingLoanApiConstants.installmentAmountParamName, newValue);
            this.installmentAmount = newValue;
        }

        if (command.isChangeInLongParameterNamed(ExistingLoanApiConstants.externalLoanPurposeIdParamName, externalLoanPurposeId())) {
            final Long newValue = command.longValueOfParameterNamed(ExistingLoanApiConstants.externalLoanPurposeIdParamName);
            actualChanges.put(ExistingLoanApiConstants.externalLoanPurposeIdParamName, newValue);
        }

        if (command.isChangeInIntegerParameterNamed(ExistingLoanApiConstants.loanStatusIdParamName, this.loanStatusId)) {
            final Integer newValue = command.integerValueOfParameterNamed(ExistingLoanApiConstants.loanStatusIdParamName);
            actualChanges.put(ExistingLoanApiConstants.loanStatusIdParamName, LoanStatus.fromInt(newValue));
            this.loanStatusId = LoanStatus.fromInt(newValue).getValue();
        }

        if (command.isChangeInLocalDateParameterNamed(ExistingLoanApiConstants.maturityDateParamName, getMaturityLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(ExistingLoanApiConstants.maturityDateParamName);
            actualChanges.put(ExistingLoanApiConstants.maturityDateParamName, valueAsInput);
            actualChanges.put(ExistingLoanApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(ExistingLoanApiConstants.localeParamName, localeAsInput);
            final LocalDate newValue = command.localDateValueOfParameterNamed(ExistingLoanApiConstants.maturityDateParamName);
            this.maturityDate = newValue.toDate();
        }

        if (command.isChangeInLocalDateParameterNamed(ExistingLoanApiConstants.disbursedDateParamName, getDisburseLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(ExistingLoanApiConstants.disbursedDateParamName);
            actualChanges.put(ExistingLoanApiConstants.disbursedDateParamName, valueAsInput);
            actualChanges.put(ExistingLoanApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(ExistingLoanApiConstants.localeParamName, localeAsInput);
            final LocalDate newValue = command.localDateValueOfParameterNamed(ExistingLoanApiConstants.disbursedDateParamName);
            this.disbursedDate = newValue.toDate();
        }

        if (command.isChangeInIntegerParameterNamed(ExistingLoanApiConstants.gt0dpd3mthsParamName, this.gt0dpd3mths)) {
            final Integer newValue = command.integerValueOfParameterNamed(ExistingLoanApiConstants.gt0dpd3mthsParamName);
            actualChanges.put(ExistingLoanApiConstants.gt0dpd3mthsParamName, newValue);
            this.gt0dpd3mths = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(ExistingLoanApiConstants.dpd30mths12ParamName, this.dpd30mths12)) {
            final Integer newValue = command.integerValueOfParameterNamed(ExistingLoanApiConstants.dpd30mths12ParamName);
            actualChanges.put(ExistingLoanApiConstants.dpd30mths12ParamName, newValue);
            this.dpd30mths12 = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(ExistingLoanApiConstants.dpd30mths24ParamName, this.dpd30mths24)) {
            final Integer newValue = command.integerValueOfParameterNamed(ExistingLoanApiConstants.dpd30mths24ParamName);
            actualChanges.put(ExistingLoanApiConstants.dpd30mths24ParamName, newValue);
            this.dpd30mths24 = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(ExistingLoanApiConstants.dpd60mths24ParamName, this.dpd60mths24)) {
            final Integer newValue = command.integerValueOfParameterNamed(ExistingLoanApiConstants.dpd60mths24ParamName);
            actualChanges.put(ExistingLoanApiConstants.dpd60mths24ParamName, newValue);
            this.dpd60mths24 = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(ExistingLoanApiConstants.archiveParamName, this.archive)) {
            final Integer newValue = command.integerValueOfParameterNamed(ExistingLoanApiConstants.archiveParamName);
            actualChanges.put(ExistingLoanApiConstants.archiveParamName, newValue);
            this.archive = newValue;
        }

        if (command.isChangeInStringParameterNamed(ExistingLoanApiConstants.remarkParamName, this.remark)) {
            final String newValue = command.stringValueOfParameterNamed(ExistingLoanApiConstants.remarkParamName);
            actualChanges.put(ExistingLoanApiConstants.remarkParamName, newValue);
            this.remark = newValue;
        }
        return actualChanges;
    }

    private Long creditBureauProductId() {
        if (this.creditBureauProduct != null) { return this.creditBureauProduct.getId(); }
        return null;
    }

    public Long sourceId() {
        Long sourceId = null;
        if (this.source != null) {
            sourceId = this.source.getId();
        }
        return sourceId;
    }

    public Long lenderId() {
        Long lenderId = null;
        if (this.lender != null) {
            lenderId = this.lender.getId();
        }
        return lenderId;
    }

    public Long loanTypeId() {
        Long loanTypeId = null;
        if (this.loanType != null) {
            loanTypeId = this.loanType.getId();
        }
        return loanTypeId;
    }

    public Long externalLoanPurposeId() {
        Long externalLoanPurposeId = null;
        if (this.externalLoanPurpose != null) {
            externalLoanPurposeId = this.externalLoanPurpose.getId();
        }
        return externalLoanPurposeId;
    }

    public LocalDate getDisburseLocalDate() {
        LocalDate disbursedOnDate = null;
        if (this.disbursedDate != null) {
            disbursedOnDate = LocalDate.fromDateFields(this.disbursedDate);
        }
        return disbursedOnDate;
    }

    public LocalDate getMaturityLocalDate() {
        LocalDate maturityLocalDate = null;
        if (this.maturityDate != null) {
            maturityLocalDate = LocalDate.fromDateFields(this.maturityDate);
        }
        return maturityLocalDate;
    }

    public void updateSourced(final CodeValue source) {
        this.source = source;
    }

    public void updateExternalLoanPurpose(CodeValue externalLoanPurpose) {
        this.externalLoanPurpose = externalLoanPurpose;
    }

    public void updateloanType(CodeValue loanType) {
        this.loanType = loanType;
    }

    public Long getClientId() {
        Long clientId = null;
        if (this.client != null) {
            clientId = this.client.getId();
        }
        return clientId;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    protected ExistingLoan() {}

    private ExistingLoan(Client client, Long loanId, Long loanApplicationId, CodeValue source,
            final CreditBureauProduct creditBureauProduct, Long loanCreditBureauEnquiryId, CodeValue lender, String lenderName,
            CodeValue loanType, BigDecimal amountBorrowed, BigDecimal currentOutstanding, BigDecimal amtOverdue,
            BigDecimal writtenOffAmount, Integer loanTenure, Integer loanTenurePeriodType, Integer repaymentFrequency,
            Integer repaymentFrequencyMultipleOf, BigDecimal installmentAmount, CodeValue externalLoanPurposeCvId, Integer loanStatusId,
            LocalDate disbursedDate, LocalDate maturityDate, final LocalDate closedDate, Integer gt0dpd3mths, Integer dpd30mths12,
            Integer dpd30mths24, Integer dpd60mths24, String remark, Integer archive) {
        this.client = client;
        this.loanId = loanId;
        this.loanApplicationId = loanApplicationId;
        this.source = source;
        this.creditBureauProduct = creditBureauProduct;
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
        this.externalLoanPurpose = externalLoanPurposeCvId;
        this.loanStatusId = loanStatusId;
        if (disbursedDate != null) {
            this.disbursedDate = disbursedDate.toDate();
        }
        if (maturityDate != null) {
            this.maturityDate = maturityDate.toDate();
        }
        if (closedDate != null) {
            this.closedDate = closedDate.toDate();
        }
        this.gt0dpd3mths = gt0dpd3mths;
        this.dpd30mths12 = dpd30mths12;
        this.dpd30mths24 = dpd30mths24;
        this.dpd60mths24 = dpd60mths24;
        this.remark = remark;
        this.archive = archive;
    }

    public void updateCreditBureauProduct(final CreditBureauProduct creditBureauProduct) {
        this.creditBureauProduct = creditBureauProduct;
    }

    public void updateLender(final CodeValue lender) {
        this.lender = lender;
    }

    public Date getClosedDate() {
        return this.closedDate;
    }

    public void setClosedDate(Date closedDate) {
        this.closedDate = closedDate;
    }
}
