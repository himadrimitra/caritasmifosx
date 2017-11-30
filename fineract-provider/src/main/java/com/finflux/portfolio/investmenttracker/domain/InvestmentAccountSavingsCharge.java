package com.finflux.portfolio.investmenttracker.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_investment_savings_account_charge")
public class InvestmentAccountSavingsCharge  extends AbstractPersistable<Long>{
    
    @ManyToOne
    @JoinColumn(name = "investment_charge_id", nullable = false)
    private InvestmentAccountCharge investmentAccountCharge;
    
    @ManyToOne
    @JoinColumn(name = "savings_linkage_account", nullable = false)
    private InvestmentAccountSavingsLinkages investmentAccountSavingsLinkages;
    
    @Column(name = "amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal amount;
    
    @Column(name = "paid_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal paidAmount;

    public InvestmentAccountSavingsCharge(InvestmentAccountCharge investmentAccountCharge,
            InvestmentAccountSavingsLinkages investmentAccountSavingsLinkages, BigDecimal amount) {
        this.investmentAccountCharge = investmentAccountCharge;
        this.investmentAccountSavingsLinkages = investmentAccountSavingsLinkages;
        this.amount = amount;
        this.paidAmount = null;
    }
    
    public static InvestmentAccountSavingsCharge create(InvestmentAccountCharge investmentAccountCharge,
            InvestmentAccountSavingsLinkages investmentAccountSavingsLinkages, BigDecimal amount){
        return new InvestmentAccountSavingsCharge(investmentAccountCharge, investmentAccountSavingsLinkages, amount);
    }

    public InvestmentAccountSavingsCharge() {
        super();
    }

    
    public InvestmentAccountCharge getInvestmentAccountCharge() {
        return this.investmentAccountCharge;
    }

    
    public void setInvestmentAccountCharge(InvestmentAccountCharge investmentAccountCharge) {
        this.investmentAccountCharge = investmentAccountCharge;
    }

    
    public InvestmentAccountSavingsLinkages getInvestmentAccountSavingsLinkages() {
        return this.investmentAccountSavingsLinkages;
    }

    
    public void setInvestmentAccountSavingsLinkages(InvestmentAccountSavingsLinkages investmentAccountSavingsLinkages) {
        this.investmentAccountSavingsLinkages = investmentAccountSavingsLinkages;
    }

    
    public BigDecimal getAmount() {
        return this.amount;
    }

    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    
    public BigDecimal getPaidAmount() {
        return this.paidAmount;
    }

    
    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }
    
    
    
    
}
