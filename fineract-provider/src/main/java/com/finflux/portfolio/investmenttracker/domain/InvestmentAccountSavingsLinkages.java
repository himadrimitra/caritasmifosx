package com.finflux.portfolio.investmenttracker.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_investment_account_savings_linkages")
public class InvestmentAccountSavingsLinkages extends AbstractPersistable<Long>{
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investment_account_id", referencedColumnName = "id", nullable = false)
    private InvestmentAccount investmentAccount;
    
    @Column(name = "account_holder", nullable = false)
    private String accountHolder;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "savings_account_id", referencedColumnName = "id", nullable = false)
    private SavingsAccount savingsAccount;
    
    @Column(name="investment_amount", precision = 19, scale = 6, nullable = false)
    private BigDecimal investmentAmount;
    
    @Column(name = "status", nullable = false)
    private Integer status;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "active_from_date", nullable = true)
    private Date activeFromDate;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "active_to_date", nullable = true)
    private Date activeToDate;
    
    public InvestmentAccountSavingsLinkages() {
        super();
    }

    public InvestmentAccountSavingsLinkages(InvestmentAccount investmentAccount, String accountHolder, SavingsAccount savingsAccount, BigDecimal investmentAmount,
            Integer status, Date activeFromDate, Date activeToDate) {
        this.savingsAccount = savingsAccount;
        this.accountHolder = accountHolder;
        this.investmentAmount = investmentAmount;
        this.investmentAccount = investmentAccount;
        this.status = status;
        this.activeFromDate = activeFromDate;
        this.activeToDate = activeToDate;
    }
    
    public Integer getStatus() {
        return this.status;
    }

    public Date getActiveFromDate() {
        return this.activeFromDate;
    }

    public Date getActiveToDate() {
        return this.activeToDate;
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

    
    public void setStatus(Integer status) {
        this.status = status;
    }

    
    public void setActiveFromDate(Date activeFromDate) {
        this.activeFromDate = activeFromDate;
    }

    
    public void setActiveToDate(Date activeToDate) {
        this.activeToDate = activeToDate;
    }
    
}
