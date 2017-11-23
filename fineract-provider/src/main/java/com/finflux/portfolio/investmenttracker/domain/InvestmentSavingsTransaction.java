package com.finflux.portfolio.investmenttracker.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_investment_saving_account_transaction")
public class InvestmentSavingsTransaction  extends AbstractPersistable<Long>{
    
    @Column(name = "savings_id", nullable = false)
    protected Long savingsId;
    
    @Column(name = "investment_id", nullable = false)
    protected Long investmentId;
    
    @Column(name = "transaction_id", nullable = false)
    protected Long transactionId;
    
    @Column(name = "description", nullable = true)
    protected String description;
    
    public InvestmentSavingsTransaction() {
        super();
    }

    protected InvestmentSavingsTransaction(Long savingsId, Long investmentId, Long transactionId, String description) {
        this.savingsId = savingsId;
        this.investmentId = investmentId;
        this.transactionId = transactionId;
        this.description = description;
    }
    
    public static InvestmentSavingsTransaction create(Long savingsId, Long investmentId, Long transactionId, String description){
        return new InvestmentSavingsTransaction(savingsId, investmentId, transactionId, description);
    }

    
    public Long getSavingsId() {
        return this.savingsId;
    }

    
    public void setSavingsId(Long savingsId) {
        this.savingsId = savingsId;
    }

    
    public Long getInvestmentId() {
        return this.investmentId;
    }

    
    public void setInvestmentId(Long investmentId) {
        this.investmentId = investmentId;
    }

    
    public Long getTransactionId() {
        return this.transactionId;
    }

    
    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    
    public String getDescription() {
        return this.description;
    }

    
    public void setDescription(String description) {
        this.description = description;
    }
    
    

}
