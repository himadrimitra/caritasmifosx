package com.finflux.risk.creditbureau.provider.highmark.data;

import com.finflux.risk.creditbureau.provider.data.LoanEnquiryData;

import java.math.BigDecimal;
import java.util.Date;

public class HighmarkEnquiryRequestData {

    final String clientName;
    final Date clientDOB;
    final String clientMobileNo;
    final BigDecimal loanAmount;
    final Long clientId;
    final Long branchId;

    public HighmarkEnquiryRequestData(final LoanEnquiryData enquiryData) {
        super();
        this.clientName = enquiryData.getClientName();
        this.clientDOB = enquiryData.getClientDOB();
        this.clientMobileNo = enquiryData.getClientMobileNo();
        this.loanAmount = enquiryData.getLoanAmount();
        this.clientId = enquiryData.getClientId();
        this.branchId = enquiryData.getBranchId();
    }

    public String getClientName() {
        return clientName;
    }

    public Date getClientDOB() {
        return clientDOB;
    }

    public String getClientMobileNo() {
        return clientMobileNo;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public Long getClientId() {
        return clientId;
    }

    public Long getBranchId() {
        return this.branchId;
    }

}
