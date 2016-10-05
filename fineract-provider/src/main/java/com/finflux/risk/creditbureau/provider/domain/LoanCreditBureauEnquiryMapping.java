package com.finflux.risk.creditbureau.provider.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.Date;

@Entity
@Table(name = "f_loan_creditbureau_enquiry")
public class LoanCreditBureauEnquiryMapping extends AbstractPersistable<Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "creditbureau_enquiry_id")
    CreditBureauEnquiry creditBureauEnquiry;

    @Column(name = "reference_num", nullable = false)
    String referenceNum;

    @Column(name = "client_id", nullable = false)
    Long ClientId;

    @Column(name = "cb_report_id", nullable = true)
    String cbReportId;

    @Column(name = "loan_id")
    Long loanId;

    @Column(name = "loan_application_id")
    Long loanApplicationId;

    @Column(name = "status")
    Integer status;

    @Column(name = "response")
    String response;

    @Column(name = "report_generated_time")
    Date reportGeneratedTtime;

    @Column(name = "file_content")
    byte[] fileContent;

    @Column(name = "file_type")
    Integer fileType;

    @Column(name = "file_name")
    String fileName;

    @Column(name = "is_active")
    boolean isActive = true;

    public LoanCreditBureauEnquiryMapping() {}

    public LoanCreditBureauEnquiryMapping(CreditBureauEnquiry creditBureauEnquiry, String refNumber, Long clientId, Long loanId,
            Long loanApplicationId, Integer status) {
        this.creditBureauEnquiry = creditBureauEnquiry;
        this.referenceNum = refNumber;
        this.ClientId = clientId;
        this.loanId = loanId;
        this.loanApplicationId = loanApplicationId;
        this.status = status;
    }

    public CreditBureauEnquiry getCreditBureauEnquiry() {
        return creditBureauEnquiry;
    }

    public void setCreditBureauEnquiry(CreditBureauEnquiry creditBureauEnquiry) {
        this.creditBureauEnquiry = creditBureauEnquiry;
    }

    public Long getClientId() {
        return this.ClientId;
    }

    public void setClientId(Long clientId) {
        this.ClientId = clientId;
    }

    public Long getLoanId() {
        return this.loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public Long getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Long loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Date getReportGeneratedTtime() {
        return reportGeneratedTtime;
    }

    public void setReportGeneratedTtime(Date reportGeneratedTtime) {
        this.reportGeneratedTtime = reportGeneratedTtime;
    }

    public String getReferenceNum() {
        return referenceNum;
    }

    public void setReferenceNum(String referenceNum) {
        this.referenceNum = referenceNum;
    }

    public Integer getFileType() {
        return fileType;
    }

    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    public String getCbReportId() {
        return cbReportId;
    }

    public void setCbReportId(String cbReportId) {
        this.cbReportId = cbReportId;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
