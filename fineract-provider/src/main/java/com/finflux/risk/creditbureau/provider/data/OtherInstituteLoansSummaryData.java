package com.finflux.risk.creditbureau.provider.data;

import java.math.BigDecimal;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class OtherInstituteLoansSummaryData {

    private EnumOptionData cbStatus;
    private byte[] cbResponse;
    private byte[] cbLoanEnqResponse;
    private EnumOptionData reportFileType;
    private BigDecimal totalAmountBorrowed;
    private BigDecimal totalCurrentOutstanding;
    private BigDecimal totalAmtOverdue;
    private BigDecimal totalInstallmentAmount;
    private String cbInitiatedDateTime;

    public OtherInstituteLoansSummaryData(final EnumOptionData cbStatus, final byte[] cbResponse, byte[] cbLoanEnqResponse,
            final BigDecimal totalAmountBorrowed, final BigDecimal totalCurrentOutstanding, final BigDecimal totalAmtOverdue,
            final BigDecimal totalInstallmentAmount, final EnumOptionData reportFileType, final String cbInitiatedDateTime) {
        this.cbStatus = cbStatus;
        this.cbResponse = cbResponse;
        this.cbLoanEnqResponse = cbLoanEnqResponse;
        this.reportFileType = reportFileType;
        this.totalAmountBorrowed = totalAmountBorrowed;
        this.totalCurrentOutstanding = totalCurrentOutstanding;
        this.totalAmtOverdue = totalAmtOverdue;
        this.totalInstallmentAmount = totalInstallmentAmount;
        this.cbInitiatedDateTime = cbInitiatedDateTime;
    }
}