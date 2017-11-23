package com.finflux.portfolio.investmenttracker.data;

import java.math.BigDecimal;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.joda.time.LocalDate;


public class InvestmentAccountSavingsLinkagesData {
    private final Long id;
    private final Long savingsAccountId;
    private final String savingsAccountNumber;
    private final Long investmentAccountId;
    private final BigDecimal individualInvestmentAmount;
    private final EnumOptionData status;
    private final LocalDate activeFrom;
    private final LocalDate activeTo;
    private List<InvestmentSavingsTransactionData> investmentSavingsTransactionData;
    List<SavingsAccountData> savingsAccounts ;
    private final String accountHolder;
    
    public InvestmentAccountSavingsLinkagesData(final Long id, final Long savingsAccountId, final String savingsAccountNumber,
            final Long investmentAccountId, final BigDecimal individualInvestmentAmount, final EnumOptionData status,
            final LocalDate activeFrom, final LocalDate activeTo, final String accountHolder){
        this.id = id;
        this.savingsAccountId = savingsAccountId;
        this.savingsAccountNumber = savingsAccountNumber;
        this.investmentAccountId = investmentAccountId;
        this.individualInvestmentAmount = individualInvestmentAmount; 
        this.status = status;
        this.activeFrom = activeFrom;
        this.activeTo = activeTo;
        this.investmentSavingsTransactionData = null;
        this.savingsAccounts = null;
        this.accountHolder = accountHolder;
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


    
    public List<InvestmentSavingsTransactionData> getInvestmentSavingsTransactionData() {
        return this.investmentSavingsTransactionData;
    }


    
    public void setInvestmentSavingsTransactionData(List<InvestmentSavingsTransactionData> investmentSavingsTransactionData) {
        this.investmentSavingsTransactionData = investmentSavingsTransactionData;
    }


    
    public List<SavingsAccountData> getSavingsAccounts() {
        return this.savingsAccounts;
    }


    
    public void setSavingsAccounts(List<SavingsAccountData> savingsAccounts) {
        this.savingsAccounts = savingsAccounts;
    }


    
    public Long getId() {
        return this.id;
    }


    
    public String getAccountHolder() {
        return this.accountHolder;
    }


    
}
