package com.finflux.risk.creditbureau.provider.data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class OtherInstituteLoansSummaryData {

    private Long loanApplicationReferenceId;
    private Long loanId;
    private EnumOptionData cbStatus;
    private byte[] cbResponse;
    private byte[] cbLoanEnqResponse;
    private EnumOptionData reportFileType;
    private BigDecimal totalAmountBorrowed;
    private BigDecimal totalCurrentOutstanding;
    private BigDecimal totalAmtOverdue;
    private BigDecimal totalInstallmentAmount;
    private String cbInitiatedDateTime;
    private final List<Map<String, String>> errors;

    public OtherInstituteLoansSummaryData(final Long loanId, final EnumOptionData cbStatus, final byte[] cbResponse,
            byte[] cbLoanEnqResponse, final BigDecimal totalAmountBorrowed, final BigDecimal totalCurrentOutstanding,
            final BigDecimal totalAmtOverdue, final BigDecimal totalInstallmentAmount, final EnumOptionData reportFileType,
            final String cbInitiatedDateTime, final List<Map<String, String>> errors) {
        this.loanId = loanId;
        this.cbStatus = cbStatus;
        this.cbResponse = cbResponse;
        this.cbLoanEnqResponse = cbLoanEnqResponse;
        this.reportFileType = reportFileType;
        this.totalAmountBorrowed = totalAmountBorrowed;
        this.totalCurrentOutstanding = totalCurrentOutstanding;
        this.totalAmtOverdue = totalAmtOverdue;
        this.totalInstallmentAmount = totalInstallmentAmount;
        this.cbInitiatedDateTime = cbInitiatedDateTime;
        this.errors = errors;
    }

    public Long getLoanApplicationReferenceId() {
        return this.loanApplicationReferenceId;
    }

    public Long getLoanId() {
        return this.loanId;
    }

    public EnumOptionData getCbStatus() {
        return this.cbStatus;
    }

    public byte[] getCbResponse() {
        return this.cbResponse;
    }

    public byte[] getCbLoanEnqResponse() {
        return this.cbLoanEnqResponse;
    }

    public EnumOptionData getReportFileType() {
        return this.reportFileType;
    }

    public BigDecimal getTotalAmountBorrowed() {
        return this.totalAmountBorrowed;
    }

    public BigDecimal getTotalCurrentOutstanding() {
        return this.totalCurrentOutstanding;
    }

    public BigDecimal getTotalAmtOverdue() {
        return this.totalAmtOverdue;
    }

    public BigDecimal getTotalInstallmentAmount() {
        return this.totalInstallmentAmount;
    }

    public String getCbInitiatedDateTime() {
        return this.cbInitiatedDateTime;
    }

    public List<Map<String, String>> getErrors() {
        return this.errors;
    }

}