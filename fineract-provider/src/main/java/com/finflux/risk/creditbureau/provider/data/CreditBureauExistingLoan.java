package com.finflux.risk.creditbureau.provider.data;

import org.apache.fineract.portfolio.calendar.domain.CalendarFrequencyType;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;

import java.util.Date;

/**
 * Created by dhirendra on 17/09/16.
 */
public class CreditBureauExistingLoan {

    final private Long clientId;
    final private Long loanApplicationId;
    final private Long loanId;
    final private Long creditBureauProductId;
    final private Long loanEnquiryId;
    private Long sourceId;
    private String lenderName;
    private Double amountDisbursed;
    private Double currentOutstanding;
    private Double amountOverdue;
    private Double writtenOffAmount;
    private Double installmentAmount;
    private String loanType;
    private Long loanTenure;
    private Long loanTenureType;
    private CalendarFrequencyType repaymentFrequency;
    private Integer repaymentMultiple;
    private Long externalLoanPurposeId;
    private LoanStatus loanStatus;
    private String receivedLoanStatus ;
    private Date disbursedDate;
    private Date closedDate;
    private String remark;

    public CreditBureauExistingLoan(final Long clientId, final Long loanApplicationId, Long loanId, Long creditBureauProductId,
            Long loanEnquiryId) {
        this.clientId = clientId;
        this.loanApplicationId = loanApplicationId;
        this.loanId = loanId;
        this.creditBureauProductId = creditBureauProductId;
        this.loanEnquiryId = loanEnquiryId;
    }

    public Long getLoanApplicationId() {
        return loanApplicationId;
    }

    public Long getLoanId() {
        return loanId;
    }

    public Long getCreditBureauProductId() {
        return creditBureauProductId;
    }

    public Long getLoanEnquiryId() {
        return loanEnquiryId;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getLenderName() {
        return lenderName;
    }

    public void setLenderName(String lenderName) {
        this.lenderName = lenderName;
    }

    public Double getAmountDisbursed() {
        return amountDisbursed;
    }

    public void setAmountDisbursed(Double amountDisbursed) {
        this.amountDisbursed = amountDisbursed;
    }

    public Double getCurrentOutstanding() {
        return currentOutstanding;
    }

    public void setCurrentOutstanding(Double currentOutstanding) {
        this.currentOutstanding = currentOutstanding;
    }

    public Double getAmountOverdue() {
        return amountOverdue;
    }

    public void setAmountOverdue(Double amountOverdue) {
        this.amountOverdue = amountOverdue;
    }

    public Double getWrittenOffAmount() {
        return writtenOffAmount;
    }

    public void setWrittenOffAmount(Double writtenOffAmount) {
        this.writtenOffAmount = writtenOffAmount;
    }

    public Double getInstallmentAmount() {
        return installmentAmount;
    }

    public void setInstallmentAmount(Double installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public Long getLoanTenure() {
        return loanTenure;
    }

    public void setLoanTenure(Long loanTenure) {
        this.loanTenure = loanTenure;
    }

    public Long getLoanTenureType() {
        return loanTenureType;
    }

    public void setLoanTenureType(Long loanTenureType) {
        this.loanTenureType = loanTenureType;
    }

    public Integer getRepaymentMultiple() {
        return repaymentMultiple;
    }

    public void setRepaymentMultiple(Integer repaymentMultiple) {
        this.repaymentMultiple = repaymentMultiple;
    }

    public Long getExternalLoanPurposeId() {
        return externalLoanPurposeId;
    }

    public void setExternalLoanPurposeId(Long externalLoanPurposeId) {
        this.externalLoanPurposeId = externalLoanPurposeId;
    }

    public LoanStatus getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(LoanStatus loanStatus) {
        this.loanStatus = loanStatus;
    }

    public void setReceivedLoanStatus(final String loanStatusString) {
    	this.receivedLoanStatus = loanStatusString ;
    }
    
    public String getReceivedLoanStatus() {
    	return this.receivedLoanStatus ;
    }
    
    public Date getDisbursedDate() {
        return disbursedDate;
    }

    public void setDisbursedDate(Date disbursedDate) {
        this.disbursedDate = disbursedDate;
    }

    public Date getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(Date closedDate) {
        this.closedDate = closedDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public CalendarFrequencyType getRepaymentFrequency() {
        return repaymentFrequency;
    }

    public void setRepaymentFrequency(CalendarFrequencyType repaymentFrequency) {
        this.repaymentFrequency = repaymentFrequency;
    }

    public Long getClientId() {
        return this.clientId;
    }

    public String getLoanType() {
        return this.loanType;
    }

    public void setLoanType(final String loanType) {
        this.loanType = loanType;
    }
}
