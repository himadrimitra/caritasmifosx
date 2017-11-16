package com.finflux.portfolio.investmenttracker.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_investment_account_savings_linkages")
public class InvestmentAccountSavingsLinkages extends AbstractPersistable<Long>{
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investment_account_id", referencedColumnName = "id", nullable = false)
    private InvestmentAccount investmentAccount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "savings_account_id", referencedColumnName = "id", nullable = false)
    private SavingsAccount savingsAccount;
    
    @Column(name="investment_amount", precision = 19, scale = 6, nullable = false)
    private BigDecimal investmentAmount;

    public InvestmentAccountSavingsLinkages(InvestmentAccount investmentAccount, SavingsAccount savingsAccount, BigDecimal investmentAmount) {
        this.savingsAccount = savingsAccount;
        this.investmentAmount = investmentAmount;
        this.investmentAccount = investmentAccount;
    }

    
    public InvestmentAccount getInvestmentAccount() {
        return this.investmentAccount;
    }

    
    public void setInvestmentAccount(InvestmentAccount investmentAccount) {
        this.investmentAccount = investmentAccount;
    }

    
    public SavingsAccount getSavingsAccount() {
        return this.savingsAccount;
    }

    
    public void setSavingsAccount(SavingsAccount savingsAccount) {
        this.savingsAccount = savingsAccount;
    }

    
    public BigDecimal getInvestmentAmount() {
        return this.investmentAmount;
    }

    
    public void setInvestmentAmount(BigDecimal investmentAmount) {
        this.investmentAmount = investmentAmount;
    }
    
}
