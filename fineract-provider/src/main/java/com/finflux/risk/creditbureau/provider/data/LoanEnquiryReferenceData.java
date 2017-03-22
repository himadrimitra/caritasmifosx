package com.finflux.risk.creditbureau.provider.data;

import java.util.Date;

import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiryStatus;

public class LoanEnquiryReferenceData {

    Long loanEnquiryId;
    Long enquiryId;
    String refNumber;
    String cbReportId;
    String acknowledgementNumber;
    Long clientId;
    Long loanId;
    Long loanApplicationId;
    Long cbProductId;
    CreditBureauEnquiryStatus status;
    Date requestedDate;
    LoanEnquiryData enquiryData;

    public LoanEnquiryReferenceData(final Long loanEnquiryId, final Long enquiryId, final String refNumber, final Long clientId,
            final Long loanId, final Long loanApplicationId, final String cbReportId, final String acknowledgementNumber,
            final CreditBureauEnquiryStatus status, final Long cbProductId, final Date requestedDate) {
        super();
        this.loanEnquiryId = loanEnquiryId;
        this.enquiryId = enquiryId;
        this.refNumber = refNumber;
        this.clientId = clientId;
        this.loanId = loanId;
        this.loanApplicationId = loanApplicationId;
        this.cbReportId = cbReportId;
        this.acknowledgementNumber = acknowledgementNumber;
        this.status = status;
        this.cbProductId = cbProductId;
        this.requestedDate = requestedDate;
    }

    public boolean isCBReportGeneratedDaysGreaterThanStalePeriod(final Integer stalePeriod) {
        if (this.getRequestedDate() != null) {
            int noOfDaysCBReportGenerated = Days.daysBetween(new LocalDate(this.getRequestedDate()), DateUtils.getLocalDateOfTenant()).getDays();
            if (noOfDaysCBReportGenerated > stalePeriod) { return true; }
        }
        return false;
    }

    public Long getLoanEnquiryId() {
        return this.loanEnquiryId;
    }

    public Long getEnquiryId() {
        return this.enquiryId;
    }

    public String getRefNumber() {
        return this.refNumber;
    }

    public String getCbReportId() {
        return this.cbReportId;
    }

    public String getAcknowledgementNumber() {
        return this.acknowledgementNumber;
    }

    public Long getClientId() {
        return this.clientId;
    }

    public Long getLoanId() {
        return this.loanId;
    }

    public Long getLoanApplicationId() {
        if (this.loanApplicationId != null && this.loanApplicationId == 0) { return null; }
        return this.loanApplicationId;
    }

    public Long getCbProductId() {
        return this.cbProductId;
    }

    public CreditBureauEnquiryStatus getStatus() {
        return this.status;
    }

    public Date getRequestedDate() {
        return this.requestedDate;
    }

    public LoanEnquiryData getEnquiryData() {
        return this.enquiryData;
    }

    public void setEnquiryData(final LoanEnquiryData enquiryData) {
        this.enquiryData = enquiryData;
    }
    
    public void setAcknowledgementNumber(final String acknowledgementNumber) {
        this.acknowledgementNumber = acknowledgementNumber;
    }

    public void setLoanEnquiryId(final Long loanEnquiryId) {
        this.loanEnquiryId = loanEnquiryId;
    }

    public void setEnquiryId(final Long enquiryId) {
        this.enquiryId = enquiryId;
    }

    public void setRefNumber(final String refNumber) {
        this.refNumber = refNumber;
    }

    public void setCbReportId(final String cbReportId) {
        this.cbReportId = cbReportId;
    }

    public void setClientId(final Long clientId) {
        this.clientId = clientId;
    }

    public void setLoanId(final Long loanId) {
        this.loanId = loanId;
    }

    public void setLoanApplicationId(final Long loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public void setCbProductId(final Long cbProductId) {
        this.cbProductId = cbProductId;
    }

    public void setStatus(final CreditBureauEnquiryStatus status) {
        this.status = status;
    }

    public void setRequestedDate(final Date requestedDate) {
        this.requestedDate = requestedDate;
    }
}