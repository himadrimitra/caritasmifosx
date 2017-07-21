package com.finflux.pdcm.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;

import com.finflux.pdcm.constants.ChequeStatus;
import com.finflux.pdcm.constants.PostDatedChequeDetailApiConstants;

@Entity
@Table(name = "f_pdc_cheque_detail")
public class PostDatedChequeDetail extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "bank_name", length = 100, nullable = false)
    private String bankName;

    @Column(name = "branch_name", length = 100, nullable = false)
    private String branchName;

    @Column(name = "account_number", length = 50, nullable = true)
    private String accountNumber;

    @Column(name = "ifsc_code", length = 50, nullable = false)
    private String ifscCode;

    @Column(name = "cheque_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal chequeAmount;

    @Column(name = "cheque_type", length = 3, nullable = false)
    private Integer chequeType;

    @Column(name = "cheque_number", length = 30, nullable = false)
    private String chequeNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "cheque_date")
    private Date chequeDate;

    @Column(name = "present_status", length = 3, nullable = false)
    private Integer presentStatus;

    @Column(name = "previous_status", length = 3, nullable = true)
    private Integer previousStatus;

    @Temporal(TemporalType.DATE)
    @Column(name = "presented_date")
    private Date presentedDate;

    @Column(name = "presented_description", length = 500, nullable = true)
    private String presentedDescription;

    @Temporal(TemporalType.DATE)
    @Column(name = "bounced_date")
    private Date bouncedDate;

    @Column(name = "bounced_description", length = 500, nullable = true)
    private String bouncedDescription;

    @Temporal(TemporalType.DATE)
    @Column(name = "cleared_date")
    private Date clearedDate;

    @Column(name = "cleared_description", length = 500, nullable = true)
    private String clearedDescription;

    @Temporal(TemporalType.DATE)
    @Column(name = "cancelled_date")
    private Date cancelledDate;

    @Column(name = "cancelled_description", length = 500, nullable = true)
    private String cancelledDescription;

    @Temporal(TemporalType.DATE)
    @Column(name = "returned_date")
    private Date returnedDate;

    @Column(name = "returned_description", length = 500, nullable = true)
    private String returnedDescription;

    @OneToOne(mappedBy = "postDatedChequeDetail", cascade = CascadeType.ALL, optional = true, orphanRemoval = true, fetch = FetchType.LAZY)
    private PostDatedChequeDetailMapping postDatedChequeDetailMapping;

    protected PostDatedChequeDetail() {}

    private PostDatedChequeDetail(final String bankName, final String branchName, final String accountNumber, final String ifscCode,
            final BigDecimal chequeAmount, final Integer chequeType, final String chequeNumber, final Date chequeDate) {
        this.bankName = bankName;
        this.branchName = branchName;
        this.accountNumber = accountNumber;
        this.ifscCode = ifscCode;
        this.chequeAmount = chequeAmount;
        this.chequeType = chequeType;
        this.chequeNumber = chequeNumber;
        this.chequeDate = chequeDate;
        this.presentStatus = ChequeStatus.PENDING.getValue();
    }

    public static PostDatedChequeDetail create(final String bankName, final String branchName, final String accountNumber,
            final String ifscCode, final BigDecimal chequeAmount, final Integer chequeType, final String chequeNumber, final Date chequeDate) {
        return new PostDatedChequeDetail(bankName, branchName, accountNumber, ifscCode, chequeAmount, chequeType, chequeNumber, chequeDate);
    }

    public PostDatedChequeDetailMapping getPostDatedChequeDetailMapping() {
        return this.postDatedChequeDetailMapping;
    }

    public void setPostDatedChequeDetailMapping(final PostDatedChequeDetailMapping postDatedChequeDetailMapping) {
        this.postDatedChequeDetailMapping = postDatedChequeDetailMapping;
    }

    public void delete() {
        this.postDatedChequeDetailMapping.delete();
    }

    public boolean isDelete() {
        return this.postDatedChequeDetailMapping.isDeleted();
    }

    public Integer getPresentStatus() {
        return this.presentStatus;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(10);

        if (command.isChangeInStringParameterNamed(PostDatedChequeDetailApiConstants.chequeNumberParamName, this.chequeNumber)) {
            final String newValue = command.stringValueOfParameterNamed(PostDatedChequeDetailApiConstants.chequeNumberParamName);
            actualChanges.put(PostDatedChequeDetailApiConstants.chequeNumberParamName, newValue);
            this.chequeNumber = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String dateFormatAsInput = command.dateFormat();
        final String localeAsInput = command.locale();
        if (command
                .isChangeInLocalDateParameterNamed(PostDatedChequeDetailApiConstants.chequeDateParamName, chequeDateParamNameLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(PostDatedChequeDetailApiConstants.chequeDateParamName);
            actualChanges.put(PostDatedChequeDetailApiConstants.chequeDateParamName, valueAsInput);
            actualChanges.put(PostDatedChequeDetailApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(PostDatedChequeDetailApiConstants.localeParamName, localeAsInput);
            final LocalDate newValue = command.localDateValueOfParameterNamed(PostDatedChequeDetailApiConstants.chequeDateParamName);
            if (newValue == null) {
                this.chequeDate = null;
            } else {
                this.chequeDate = newValue.toDate();
            }
        }

        if (command.isChangeInBigDecimalParameterNamed(PostDatedChequeDetailApiConstants.chequeAmountParamName, this.chequeAmount)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(PostDatedChequeDetailApiConstants.chequeAmountParamName);
            actualChanges.put(PostDatedChequeDetailApiConstants.chequeAmountParamName, newValue);
            this.chequeAmount = newValue;
        }

        if (command.isChangeInStringParameterNamed(PostDatedChequeDetailApiConstants.bankNameParamName, this.bankName)) {
            final String newValue = command.stringValueOfParameterNamed(PostDatedChequeDetailApiConstants.bankNameParamName);
            actualChanges.put(PostDatedChequeDetailApiConstants.bankNameParamName, newValue);
            this.bankName = newValue;
        }

        if (command.isChangeInStringParameterNamed(PostDatedChequeDetailApiConstants.branchNameParamName, this.branchName)) {
            final String newValue = command.stringValueOfParameterNamed(PostDatedChequeDetailApiConstants.branchNameParamName);
            actualChanges.put(PostDatedChequeDetailApiConstants.branchNameParamName, newValue);
            this.branchName = newValue;
        }

        if (command.isChangeInStringParameterNamed(PostDatedChequeDetailApiConstants.ifscCodeParamName, this.ifscCode)) {
            final String newValue = command.stringValueOfParameterNamed(PostDatedChequeDetailApiConstants.ifscCodeParamName);
            actualChanges.put(PostDatedChequeDetailApiConstants.ifscCodeParamName, newValue);
            this.ifscCode = newValue;
        }

        return actualChanges;
    }

    public LocalDate chequeDateParamNameLocalDate() {
        LocalDate chequeDate = null;
        if (this.chequeDate != null) {
            chequeDate = LocalDate.fromDateFields(this.chequeDate);
        }
        return chequeDate;
    }

    public String getBankName() {
        return this.bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBranchName() {
        return this.branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIfscCode() {
        return this.ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public BigDecimal getChequeAmount() {
        return this.chequeAmount;
    }

    public void setChequeAmount(BigDecimal chequeAmount) {
        this.chequeAmount = chequeAmount;
    }

    public Integer getChequeType() {
        return this.chequeType;
    }

    public void setChequeType(Integer chequeType) {
        this.chequeType = chequeType;
    }

    public String getChequeNumber() {
        return this.chequeNumber;
    }

    public void setChequeNumber(String chequeNumber) {
        this.chequeNumber = chequeNumber;
    }

    public Date getChequeDate() {
        return this.chequeDate;
    }

    public void setChequeDate(Date chequeDate) {
        this.chequeDate = chequeDate;
    }

    public Integer getPreviousStatus() {
        return this.previousStatus;
    }

    public void setPreviousStatus(Integer previousStatus) {
        this.previousStatus = previousStatus;
    }

    public Date getPresentedDate() {
        return this.presentedDate;
    }

    public LocalDate getPresentedLocalDate() {
        LocalDate presentedDate = null;
        if (this.presentedDate != null) {
            presentedDate = LocalDate.fromDateFields(this.presentedDate);
        }
        return presentedDate;
    }

    public void setPresentedDate(Date presentedDate) {
        this.presentedDate = presentedDate;
    }

    public String getPresentedDescription() {
        return this.presentedDescription;
    }

    public void setPresentedDescription(String presentedDescription) {
        this.presentedDescription = presentedDescription;
    }

    public Date getBouncedDate() {
        return this.bouncedDate;
    }

    public void setBouncedDate(Date bouncedDate) {
        this.bouncedDate = bouncedDate;
    }

    public String getBouncedDescription() {
        return this.bouncedDescription;
    }

    public void setBouncedDescription(String bouncedDescription) {
        this.bouncedDescription = bouncedDescription;
    }

    public Date getClearedDate() {
        return this.clearedDate;
    }

    public void setClearedDate(Date clearedDate) {
        this.clearedDate = clearedDate;
    }

    public String getClearedDescription() {
        return this.clearedDescription;
    }

    public void setClearedDescription(String clearedDescription) {
        this.clearedDescription = clearedDescription;
    }

    public Date getCancelledDate() {
        return this.cancelledDate;
    }

    public void setCancelledDate(Date cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public String getCancelledDescription() {
        return this.cancelledDescription;
    }

    public void setCancelledDescription(String cancelledDescription) {
        this.cancelledDescription = cancelledDescription;
    }

    public Date getReturnedDate() {
        return this.returnedDate;
    }

    public void setReturnedDate(Date returnedDate) {
        this.returnedDate = returnedDate;
    }

    public String getReturnedDescription() {
        return this.returnedDescription;
    }

    public void setReturnedDescription(String returnedDescription) {
        this.returnedDescription = returnedDescription;
    }

    public void setPresentStatus(Integer presentStatus) {
        this.previousStatus = this.presentStatus;
        this.presentStatus = presentStatus;
    }
}