package com.finflux.risk.creditbureau.provider.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoanEnquiryData {

    final String clientName;
    final String clientFirstName;
    final String clientMiddleName;
    final String clientLastName;
    final Date clientDOB;
    List<EnquiryAddressData> addressList;
    List<EnquiryDocumentData> documentList;
    final String clientMobileNo;
    final Long loanProductId;
    String loanApplicationId;
    final BigDecimal loanAmount;
    final Long clientId;
    final Long branchId;

    public LoanEnquiryData(final String clientName, final Date clientDOB, final String clientMobileNo, final Long loanProductId,
            final BigDecimal loanAmount, final Long clientId, Long branchId, final String firstName, final String middlename,
            final String lastName) {
        super();
        this.clientName = clientName;
        this.clientDOB = clientDOB;
        this.clientMobileNo = clientMobileNo;
        this.loanProductId = loanProductId;
        this.loanAmount = loanAmount;
        this.clientId = clientId;
        this.branchId = branchId;
        this.clientFirstName = firstName;
        this.clientLastName = lastName;
        this.clientMiddleName = middlename;
        addressList = new ArrayList<>();
        documentList = new ArrayList<>();
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

    public Long getLoanProductId() {
        return loanProductId;
    }

    public List<EnquiryAddressData> getAddressList() {
        return addressList;
    }

    public List<EnquiryDocumentData> getDocumentList() {
        return documentList;
    }

    public void setAddressList(List<EnquiryAddressData> addressList) {
        this.addressList = addressList;
    }

    public void setDocumentList(List<EnquiryDocumentData> documentList) {
        this.documentList = documentList;
    }

    public String getLoanApplicationId() {
        return loanApplicationId;
    }

    public String getClientFirstName() {
        return clientFirstName;
    }

    public String getClientMiddleName() {
        return clientMiddleName;
    }

    public String getClientLastName() {
        return clientLastName;
    }

    public void setLoanApplicationId(String loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }
}
