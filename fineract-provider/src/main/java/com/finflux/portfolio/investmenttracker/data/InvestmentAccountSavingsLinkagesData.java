package com.finflux.portfolio.investmenttracker.data;

import java.math.BigDecimal;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.joda.time.LocalDate;


public class InvestmentAccountSavingsLinkagesData {
    
    private final Long savingsAccountId;
    private final String savingsAccountNumber;
    private final Long investmentAccountId;
    private final BigDecimal individualInvestmentAmount;
    private final EnumOptionData status;
    private final LocalDate activeFrom;
    private final LocalDate activeTo;
    
    public InvestmentAccountSavingsLinkagesData(final Long savingsAccountId, final String savingsAccountNumber,
            final Long investmentAccountId, final BigDecimal individualInvestmentAmount, final EnumOptionData status,
            final LocalDate activeFrom, final LocalDate activeTo){
        this.savingsAccountId = savingsAccountId;
        this.savingsAccountNumber = savingsAccountNumber;
        this.investmentAccountId = investmentAccountId;
        this.individualInvestmentAmount = individualInvestmentAmount; 
        this.status = status;
        this.activeFrom = activeFrom;
        this.activeTo = activeTo;
    }

    
    public Long getSavingsAccountId() {
        return this.savingsAccountId;
    }

    
    public String getSavingsAccountNumber() {
        return this.savingsAccountNumber;
    }

    
    public Long getInvestmentAccountId() {
        return this.investmentAccountId;
    }

    
    public BigDecimal getIndividualInvestmentAmount() {
        return this.individualInvestmentAmount;
    }

    public EnumOptionData getStatus() {
        return this.status;
    }

    
    public LocalDate getActiveFrom() {
        return this.activeFrom;
    }


    
    public LocalDate getActiveTo() {
        return this.activeTo;
    }

    
}
