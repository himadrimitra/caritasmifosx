package com.finflux.risk.creditbureau.provider.data;

import com.finflux.risk.creditbureau.provider.domain.CreditBureauEnquiryStatus;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class EnquiryReferenceData {

    final Long enquiryId;
    final String acknowledgementNumber;
    final CreditBureauEnquiryStatus creditBureauEnquiryStatus;
    final String type;
    List<LoanEnquiryReferenceData> loansReferenceData;
    final Long creditBureauProductId;
    final Date requestedDate;

    public EnquiryReferenceData(final Long enquiryId, final String acknowledgementNumber, CreditBureauEnquiryStatus status, String type,
            Date requestedDate, Long creditBureauProductId) {
        super();
        this.enquiryId = enquiryId;
        this.acknowledgementNumber = acknowledgementNumber;
        this.creditBureauEnquiryStatus = status;
        this.type = type;
        this.requestedDate = requestedDate;
        this.creditBureauProductId = creditBureauProductId;
    }

    public Long getEnquiryId() {
        return enquiryId;
    }

    public String getAcknowledgementNumber() {
        return acknowledgementNumber;
    }

    public CreditBureauEnquiryStatus getCreditBureauEnquiryStatus() {
        return creditBureauEnquiryStatus;
    }

    public String getType() {
        return type;
    }

    public List<LoanEnquiryReferenceData> getLoansReferenceData() {
        return loansReferenceData;
    }

    public void setLoansReferenceData(List<LoanEnquiryReferenceData> loansReferenceData) {
        this.loansReferenceData = loansReferenceData;
    }

    public Date getRequestedDate() {
        return requestedDate;
    }

    public Long getCreditBureauProductId() {
        return creditBureauProductId;
    }
}
