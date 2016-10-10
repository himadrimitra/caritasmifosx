package com.finflux.risk.creditbureau.provider.highmark.xsd.old.response;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlElement;

public class LoanDetails {

    @XmlElement(name = "CURRENT-BAL")
    protected BigDecimal loanBalance;

    @XmlElement(name = "OVERDUE-AMT")
    protected BigDecimal overdueAmount;

    @XmlElement(name = "WORST-DELEQUENCY-AMOUNT")
    protected BigDecimal DelenquencyAmount;

    @XmlElement(name = "STATUS")
    protected String status;

    @XmlElement(name = "INSTALLMENT-AMT")
    protected BigDecimal installmentAmount;

    @XmlElement(name = "FREQ")
    protected String frequency;

    public BigDecimal getLoanBalance() {
        return this.loanBalance;
    }

    public BigDecimal getOverdueAmount() {
        return this.overdueAmount;
    }

    public BigDecimal getDelenquencyAmount() {
        return this.DelenquencyAmount;
    }

    public String getStatus() {
        return this.status;
    }

    public BigDecimal getInstallmentAmount() {
        return this.installmentAmount;
    }

    public String getFrequency() {
        return this.frequency;
    }

}
