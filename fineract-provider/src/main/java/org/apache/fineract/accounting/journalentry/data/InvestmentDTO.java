package org.apache.fineract.accounting.journalentry.data;

import java.util.List;


public class InvestmentDTO {
    
    private Long investmentId;
    private Long investmentProductId;
    private Long officeId;
    private String currencyCode;
    private boolean cashBasedAccountingEnabled;
    private List<InvestmentTransactionDTO> newInvestmentTransactions;
    

    public InvestmentDTO(Long investmentId, Long investmentProductId, Long officeId, String currencyCode,
            boolean cashBasedAccountingEnabled, List<InvestmentTransactionDTO> newInvestmentTransactions) {
        super();
        this.investmentId = investmentId;
        this.investmentProductId = investmentProductId;
        this.officeId = officeId;
        this.currencyCode = currencyCode;
        this.cashBasedAccountingEnabled = cashBasedAccountingEnabled;
        this.newInvestmentTransactions = newInvestmentTransactions;
    }




    public Long getInvestmentId() {
        return this.investmentId;
    }

    
    public void setInvestmentId(Long investmentId) {
        this.investmentId = investmentId;
    }

    
    public Long getInvestmentProductId() {
        return this.investmentProductId;
    }

    
    public void setInvestmentProductId(Long investmentProductId) {
        this.investmentProductId = investmentProductId;
    }

    
    public Long getOfficeId() {
        return this.officeId;
    }

    
    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
    }

    
    public String getCurrencyCode() {
        return this.currencyCode;
    }

    
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    
    public boolean isCashBasedAccountingEnabled() {
        return this.cashBasedAccountingEnabled;
    }

    
    public void setCashBasedAccountingEnabled(boolean cashBasedAccountingEnabled) {
        this.cashBasedAccountingEnabled = cashBasedAccountingEnabled;
    }




    
    public List<InvestmentTransactionDTO> getNewInvestmentTransactions() {
        return this.newInvestmentTransactions;
    }




    
    public void setNewInvestmentTransactions(List<InvestmentTransactionDTO> newInvestmentTransactions) {
        this.newInvestmentTransactions = newInvestmentTransactions;
    }

    

}
