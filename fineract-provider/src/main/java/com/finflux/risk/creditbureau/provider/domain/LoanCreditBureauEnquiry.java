package com.finflux.risk.creditbureau.provider.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_loan_creditbureau_enquiry")
public class LoanCreditBureauEnquiry extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "creditbureau_enquiry_id", nullable = false)
    CreditBureauEnquiry creditBureauEnquiry;

    @Column(name = "reference_num", nullable = false)
    String referenceNum;

    @Column(name = "client_id", nullable = false)
    Long clientId;

    @Column(name = "cb_report_id", nullable = true)
    String cbReportId;

    @Column(name = "loan_id")
    Long loanId;

    @Column(name = "loan_application_id")
    Long loanApplicationId;

    @Column(name = "tranche_disbursal_id")
    Long trancheDisbursalId;

    @Column(name = "status")
    Integer status;

    @Column(name = "request")
    String request;

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

    public LoanCreditBureauEnquiry() {}

    public LoanCreditBureauEnquiry(CreditBureauEnquiry creditBureauEnquiry, String refNumber, Long clientId, Long loanId,
            Long loanApplicationId, Long trancheDisbursalId, Integer status) {
        this.creditBureauEnquiry = creditBureauEnquiry;
        this.referenceNum = refNumber;
        this.clientId = clientId;
        this.loanId = loanId;
        this.loanApplicationId = loanApplicationId;
        this.trancheDisbursalId = trancheDisbursalId;
        this.status = status;
        this.isActive = true;
    }

    public CreditBureauEnquiry getCreditBureauEnquiry() {
        return creditBureauEnquiry;
    }

    public void setCreditBureauEnquiry(CreditBureauEnquiry creditBureauEnquiry) {
        this.creditBureauEnquiry = creditBureauEnquiry;
    }

    public Long getClientId() {
        return this.clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
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

    public void update(final String refNumber, final Long clientId, final Long loanId, final Long loanApplicationId,
            final Long trancheDisbursalId, final Integer status) {
        this.referenceNum = refNumber;
        this.clientId = clientId;
        this.loanId = loanId;
        this.loanApplicationId = loanApplicationId;
        this.trancheDisbursalId = trancheDisbursalId;
        this.status = status;
        this.isActive = true;
    }

    public String getRequest() {
        return this.request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public Long getTrancheDisbursalId() {
        return this.trancheDisbursalId;
    }

    public void setTrancheDisbursalId(Long trancheDisbursalId) {
        this.trancheDisbursalId = trancheDisbursalId;
    }
}