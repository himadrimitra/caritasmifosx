package com.finflux.portfolio.investmenttracker.data;

import java.math.BigDecimal;


public class InvestmentAccountSavingsLinkagesData {
    
    private final Long savingsAccountId;
    private final String savingsAccountNumber;
    private final Long investmentAccountId;
    private final BigDecimal individualInvestmentAmount;
    
    public InvestmentAccountSavingsLinkagesData(final Long savingsAccountId, final String savingsAccountNumber,
            final Long investmentAccountId, final BigDecimal individualInvestmentAmount){
        this.savingsAccountId = savingsAccountId;
        this.savingsAccountNumber = savingsAccountNumber;
        this.investmentAccountId = investmentAccountId;
        this.individualInvestmentAmount = individualInvestmentAmount;     
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

}
