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
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.finflux.risk.creditbureau.configuration.domain.CreditBureauProduct;

@Entity
@Table(name = "f_creditbureau_enquiry")
public class CreditBureauEnquiry extends AbstractAuditableCustom<AppUser, Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "creditbureau_product_id", nullable = false)
    private CreditBureauProduct creditBureauProduct;

    @Column(name = "type", length = 20, nullable = false)
    private String type;

    @Column(name = "request_location")
    private String requestLocation;

    @Column(name = "response_location")
    private String responseLocation;
    
    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "acknowledgement_num")
    private String acknowledgementNumber;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "creditBureauEnquiry", orphanRemoval = true)
    List<LoanCreditBureauEnquiry> loanCreditBureauEnquiries = new ArrayList<>();

    @Column(name = "errors_json", nullable = true) 
    private String errorsJson ;
    
    protected CreditBureauEnquiry() {}

    public CreditBureauEnquiry(final CreditBureauProduct creditBureauProduct, final String type, final Integer status) {
        this.creditBureauProduct = creditBureauProduct;
        this.type = type;
        this.status = status;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(final Integer status) {
        this.status = status;
    }

    public List<LoanCreditBureauEnquiry> getLoanCreditBureauEnquiryMapping() {
        return loanCreditBureauEnquiries;
    }

    public void setLoanCreditBureauEnquiries(final List<LoanCreditBureauEnquiry> loanCreditBureauEnquiries) {
        this.loanCreditBureauEnquiries.clear();
        this.loanCreditBureauEnquiries.addAll(loanCreditBureauEnquiries);
    }

    public CreditBureauProduct getCreditBureauProduct() {
        return creditBureauProduct;
    }

    public void setCreditBureauProduct(final CreditBureauProduct creditBureauProduct) {
        this.creditBureauProduct = creditBureauProduct;
    }

    public String getAcknowledgementNumber() {
        return acknowledgementNumber;
    }

    public void setAcknowledgementNumber(final String acknowledgementNumber) {
        this.acknowledgementNumber = acknowledgementNumber;
    }

    public void setErrorsJosnString(final String errorsJson) {
        this.errorsJson = errorsJson ;
    }
    
    public void setRequestLocation(final String requestLocation) {
    	this.requestLocation = requestLocation ;
    }
    
    public void setResponseLocation(final String responseLocation) {
    	this.responseLocation = responseLocation ;
    }
    
    public String getRequestLocation() {
    	return this.requestLocation ;
    }
    
    public String getResponseLocation() {
    	return this.responseLocation ;
    }
}