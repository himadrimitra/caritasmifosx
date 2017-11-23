package com.finflux.portfolio.investmenttracker.data;

import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionData;

public class InvestmentSavingsTransactionData {

    final Long id;
    final SavingsAccountTransactionData savingsAccountTransactionData;
    final Long investmentId;
    final String description;
    final OfficeData officeData;

    public InvestmentSavingsTransactionData(final Long id, final SavingsAccountTransactionData savingsAccountTransactionData,
            final Long investmentId, final String description,final OfficeData officeData) {
        this.id = id;
        this.savingsAccountTransactionData = savingsAccountTransactionData;
        this.investmentId = investmentId;
        this.description = description;
        this.officeData = officeData;
    }

    public static InvestmentSavingsTransactionData create(final Long id, final SavingsAccountTransactionData savingsAccountTransactionData,
            final Long investmentId, final String description,final OfficeData officeData) {
        return new InvestmentSavingsTransactionData(id, savingsAccountTransactionData, investmentId, description, officeData);
    }

    public SavingsAccountTransactionData getSavingsAccountTransactionData() {
        return this.savingsAccountTransactionData;
    }

    public Long getInvestmentId() {
        return this.investmentId;
    }

    public Long getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

     
    public OfficeData getOfficeData() {
        return this.officeData;
    }
    
    

}
