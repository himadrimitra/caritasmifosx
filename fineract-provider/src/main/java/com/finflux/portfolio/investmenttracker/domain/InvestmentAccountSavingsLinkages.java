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

import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.joda.time.LocalDate;
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
    
    @Column(name="expected_interest_amount", precision = 19, scale = 6, nullable = true)
    private BigDecimal expectedInterestAmount;
    
    @Column(name="expected_charge_amount", precision = 19, scale = 6, nullable = true)
    private BigDecimal expectedChargeAmount;
    
    @Column(name="expected_maturity_amount", precision = 19, scale = 6, nullable = true)
    private BigDecimal expectedMaturityAmount;
    
    @Column(name="interest_amount", precision = 19, scale = 6, nullable = true)
    private BigDecimal interestAmount;
    
    @Column(name="charge_amount", precision = 19, scale = 6, nullable = true)
    private BigDecimal chargeAmount;
    
    @Column(name="maturity_amount", precision = 19, scale = 6, nullable = true)
    private BigDecimal maturityAmount;
    
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
            Integer status) {
        this.savingsAccount = savingsAccount;
        this.accountHolder = accountHolder;
        this.investmentAmount = investmentAmount;
        this.investmentAccount = investmentAccount;
        this.status = status;
        this.activeFromDate = null;
        this.activeToDate = null;
        this.expectedInterestAmount = null;
        this.expectedChargeAmount = null;
        this.expectedMaturityAmount = null;
        this.chargeAmount = null;
        this.maturityAmount = null;
        this.interestAmount = null;
    }
    
    public InvestmentAccountSavingsLinkages(InvestmentAccountSavingsLinkages investmentSavingAccount,SavingsAccount savingsAccount, Date activeFromDate) {
        this.savingsAccount = savingsAccount;
        this.accountHolder = savingsAccount.getClient()==null?savingsAccount.getGroup().getName():savingsAccount.getClient().getDisplayName();
        this.investmentAmount = investmentSavingAccount.getInvestmentAmount();
        this.investmentAccount = investmentSavingAccount.getInvestmentAccount();
        this.status = InvestmentAccountStatus.ACTIVE.getValue();
        this.activeFromDate = activeFromDate;
        this.activeToDate = investmentSavingAccount.getInvestmentAccount().getMaturityOnDate();
        this.expectedInterestAmount = MathUtility.subtract(investmentSavingAccount.getExpectedInterestAmount(), investmentSavingAccount.getInterestAmount());
        this.expectedChargeAmount = MathUtility.subtract(investmentSavingAccount.getExpectedChargeAmount(), investmentSavingAccount.getChargeAmount());
        this.expectedMaturityAmount = MathUtility.subtract(MathUtility.add(this.investmentAmount,this.expectedInterestAmount),this.expectedChargeAmount);
        this.chargeAmount = null;
        this.maturityAmount = null;
        this.interestAmount = null;
    }
    
    public Integer getStatus() {
        return this.status;
    }

    public LocalDate getActiveFromDate() {
        return new LocalDate(this.activeFromDate);
    }

    public LocalDate getActiveToDate() {
        return new LocalDate(this.activeToDate);
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

    
    public void updateInvestmentAmount(BigDecimal investmentAmount) {
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

    
    public String getAccountHolder() {
        return this.accountHolder;
    }

    
    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    
    public BigDecimal getExpectedInterestAmount() {
        return this.expectedInterestAmount;
    }

    
    public void setExpectedInterestAmount(BigDecimal expectedInterestAmount) {
        this.expectedInterestAmount = expectedInterestAmount;
    }

    
    public BigDecimal getExpectedChargeAmount() {
        return this.expectedChargeAmount;
    }

    
    public void setExpectedChargeAmount(BigDecimal expectedChargeAmount) {
        this.expectedChargeAmount = expectedChargeAmount;
    }

    
    public BigDecimal getExpectedMaturityAmount() {
        return this.expectedMaturityAmount;
    }

    
    public void setExpectedMaturityAmount(BigDecimal expectedMaturityAmount) {
        this.expectedMaturityAmount = expectedMaturityAmount;
    }

    
    public BigDecimal getInterestAmount() {
        return this.interestAmount;
    }

    
    public void setInterestAmount(BigDecimal interestAmount) {
        this.interestAmount = interestAmount;
    }

    
    public BigDecimal getChargeAmount() {
        return this.chargeAmount;
    }

    
    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    
    public BigDecimal getMaturityAmount() {
        return this.maturityAmount;
    }

    
    public void setMaturityAmount(BigDecimal maturityAmount) {
        this.maturityAmount = maturityAmount;
    }
    
    public boolean isActive(){
        return this.status.equals(InvestmentAccountStatus.ACTIVE.getValue());
    }
    
    public void updateExpectedCharge(BigDecimal amount){
        this.expectedChargeAmount = MathUtility.add(this.expectedChargeAmount, amount);
    }
    
}
