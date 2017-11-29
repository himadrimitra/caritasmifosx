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
    private  BigDecimal individualInvestmentAmount;
    private final EnumOptionData status;
    private final LocalDate activeFrom;
    private final LocalDate activeTo;
    private List<InvestmentSavingsTransactionData> investmentSavingsTransactionData;
    List<SavingsAccountData> savingsAccounts ;
    private final String accountHolder;
    private final BigDecimal expectedInterestAmount;
    private final BigDecimal expectedChargeAmount;
    private final BigDecimal expectedMaturityAmount;
    private final BigDecimal interestAmount;
    private final BigDecimal chargeAmount;
    private final BigDecimal maturityAmount;
    
    public InvestmentAccountSavingsLinkagesData(final Long id, final Long savingsAccountId, final String savingsAccountNumber,
            final Long investmentAccountId, final BigDecimal individualInvestmentAmount, final EnumOptionData status,
            final LocalDate activeFrom, final LocalDate activeTo, final String accountHolder, final BigDecimal expectedInterestAmount,
            final BigDecimal expectedChargeAmount,final BigDecimal expectedMaturityAmount, final BigDecimal interestAmount,
            final BigDecimal chargeAmount, final BigDecimal maturityAmount){
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
        this.expectedChargeAmount = expectedChargeAmount;
        this.expectedInterestAmount = expectedInterestAmount;
        this.expectedMaturityAmount = expectedMaturityAmount;
        this.chargeAmount = chargeAmount;
        this.interestAmount = interestAmount;
        this.maturityAmount = maturityAmount;
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


    
    public BigDecimal getExpectedInterestAmount() {
        return this.expectedInterestAmount;
    }


    
    public BigDecimal getExpectedChargeAmount() {
        return this.expectedChargeAmount;
    }


    
    public BigDecimal getExpectedMaturityAmount() {
        return this.expectedMaturityAmount;
    }


    
    public BigDecimal getInterestAmount() {
        return this.interestAmount;
    }


    
    public BigDecimal getChargeAmount() {
        return this.chargeAmount;
    }


    
    public BigDecimal getMaturityAmount() {
        return this.maturityAmount;
    }
    
    public void setIndividualInvestmentAmount(BigDecimal individualInvestmentAmount) {
        this.individualInvestmentAmount = individualInvestmentAmount;
    }
   
}
