package com.finflux.risk.creditbureau.provider.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;

import com.finflux.risk.creditbureau.configuration.domain.CreditBureauProduct;

@Entity
@Table(name = "f_creditbureau_enquiry")
public class CreditBureauEnquiry extends AbstractAuditableCustom<AppUser, Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "creditbureau_product_id", nullable = false)
    private CreditBureauProduct creditBureauProduct;

    @Column(name = "type", length = 20, nullable = false)
    private String type;

    @Column(name = "request")
    private String request;

    @Column(name = "response")
    private String response;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "acknowledgement_num")
    private String acknowledgementNumber;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "creditBureauEnquiry")
    List<LoanCreditBureauEnquiryMapping> loanCreditBureauEnquiryMapping = new ArrayList<>();

    public CreditBureauEnquiry() {}

    //
    public CreditBureauEnquiry(CreditBureauProduct creditBureauProduct, String type, Integer status,
            List<LoanCreditBureauEnquiryMapping> loanCreditBureauEnquiryMapping) {

        this.creditBureauProduct = creditBureauProduct;
        this.type = type;
        this.status = status;
        this.loanCreditBureauEnquiryMapping = loanCreditBureauEnquiryMapping;
    }

    //
    // public CreditBureauEnquiry(String type, String request, Date dateTime,
    // String status, Date statusDateTime,
    // String lastRequestType, Date lastRequestDateTime, String batchId, String
    // enquiryRefNumber, String fileName, String response) {
    // this.type = type;
    // this.request = request;
    // this.dateTime = dateTime;
    // this.status = status;
    // this.statusDateTime = statusDateTime;
    // this.lastRequestType = lastRequestType;
    // this.lastRequestDateTime = lastRequestDateTime;
    // this.batchId = batchId;
    // this.enquiryRefNumber = enquiryRefNumber;
    // this.fileName = fileName;
    // this.response = response;
    // }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRequest() {
        return this.request;
    }

    public void setRequest(String full_request) {
        this.request = full_request;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<LoanCreditBureauEnquiryMapping> getLoanCreditBureauEnquiryMapping() {
        return loanCreditBureauEnquiryMapping;
    }

    public void setLoanCreditBureauEnquiryMapping(List<LoanCreditBureauEnquiryMapping> loanCreditBureauEnquiryMapping) {
        this.loanCreditBureauEnquiryMapping = loanCreditBureauEnquiryMapping;
    }

    public CreditBureauProduct getCreditBureauProduct() {
        return creditBureauProduct;
    }

    public void setCreditBureauProduct(CreditBureauProduct creditBureauProduct) {
        this.creditBureauProduct = creditBureauProduct;
    }

    public String getAcknowledgementNumber() {
        return acknowledgementNumber;
    }

    public void setAcknowledgementNumber(String acknowledgementNumber) {
        this.acknowledgementNumber = acknowledgementNumber;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}