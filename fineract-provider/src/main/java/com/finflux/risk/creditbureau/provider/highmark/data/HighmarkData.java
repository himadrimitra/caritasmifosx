package com.finflux.risk.creditbureau.provider.highmark.data;

import java.math.BigDecimal;
import java.util.Date;

public class HighmarkData {

    final Long activeLoanCount;
    final Long closedLoanCount;
    final Long delinquentLoanCount;
    final BigDecimal totalOutstanding;
    final BigDecimal totalOverdues;
    final BigDecimal totalInstallments;
    final Date responseDate;

    public HighmarkData(Long activeLoanCount, Long closedLoanCount, Long delinquentLoanCount, BigDecimal totalOutstanding,
            BigDecimal totalOverdues, BigDecimal totalInstallments, Date responseDate) {
        this.activeLoanCount = activeLoanCount;
        this.closedLoanCount = closedLoanCount;
        this.delinquentLoanCount = delinquentLoanCount;
        this.totalOutstanding = totalOutstanding;
        this.totalOverdues = totalOverdues;
        this.totalInstallments = totalInstallments;
        this.responseDate = responseDate;
    }

    public Long getActiveLoanCount() {
        return this.activeLoanCount;
    }

    public Long getClosedLoanCount() {
        return this.closedLoanCount;
    }

    public Long getDelinquentLoanCount() {
        return this.delinquentLoanCount;
    }

    public BigDecimal getTotalOutstanding() {
        return this.totalOutstanding;
    }

    public BigDecimal getTotalOverdues() {
        return this.totalOverdues;
    }

    public BigDecimal getTotalInstallments() {
        return this.totalInstallments;
    }

    public Date getResponseDate() {
        return this.responseDate;
    }

}
