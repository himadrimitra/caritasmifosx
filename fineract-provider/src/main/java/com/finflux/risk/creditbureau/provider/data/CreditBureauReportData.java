package com.finflux.risk.creditbureau.provider.data;

import java.math.BigDecimal;
import java.util.Date;

public class CreditBureauReportData {

    final String clientName;
    final Date clientDOB;
    final String clientIdentificationType;
    final String clientIdentification;
    final String clientMobileNo;
    final String clientAddressType;
    final String clientAddress;
    final String clientCity;
    final String clientState;
    final String clientPin;
    final Long loanId;
    final BigDecimal loanAmount;
    final Long clientId;
    final Long branchId;

    public CreditBureauReportData(final String clientName, final Date clientDOB, final String clientIdentificationType,
            final String clientIdentification, final String clientMobileNo, final String clientAddressType, final String clientAddress,
            final String clientCity, final String clientState, final String clientPin, final Long loanId, final BigDecimal loanAmount,
            final Long clientId, Long branchId) {
        super();
        this.clientName = clientName;
        this.clientDOB = clientDOB;
        this.clientIdentificationType = clientIdentificationType;
        this.clientIdentification = clientIdentification;
        this.clientMobileNo = clientMobileNo;
        this.clientAddressType = clientAddressType;
        this.clientAddress = clientAddress;
        this.clientCity = clientCity;
        this.clientState = clientState;
        this.clientPin = clientPin;
        this.loanId = loanId;
        this.loanAmount = loanAmount;
        this.clientId = clientId;
        this.branchId = branchId;
    }

    public String getClientName() {
        return clientName;
    }

    public Date getClientDOB() {
        return clientDOB;
    }

    public String getClientIdentificationType() {
        return clientIdentificationType;
    }

    public String getClientIdentification() {
        return clientIdentification;
    }

    public String getClientMobileNo() {
        return clientMobileNo;
    }

    public String getClientAddressType() {
        return clientAddressType;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public String getClientCity() {
        return clientCity;
    }

    public String getClientState() {
        return clientState;
    }

    public String getClientPin() {
        return clientPin;
    }

    public Long getLoanId() {
        return loanId;
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