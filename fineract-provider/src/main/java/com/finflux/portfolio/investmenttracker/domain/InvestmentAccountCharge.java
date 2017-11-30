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

import org.apache.fineract.portfolio.charge.domain.Charge;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_investment_account_charge")
public class InvestmentAccountCharge extends AbstractPersistable<Long>{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investment_account_id", referencedColumnName = "id", nullable = false)
    private InvestmentAccount investmentAccount;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "charge_id", referencedColumnName = "id", nullable = false)
    private Charge charge;
    
    @Column(name = "is_penalty", nullable = false)
    private boolean penaltyCharge = false;

    @Column(name = "is_active", nullable = false)
    private boolean status = true;
    
    @Column(name = "amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal amount;

    @Temporal(TemporalType.DATE)
    @Column(name = "inactivated_on_date")
    private Date inactivationDate;

    public InvestmentAccountCharge(InvestmentAccount investmentAccount, Charge charge, BigDecimal amount, boolean penaltyCharge, boolean status,
            Date inactivationDate) {
        this.investmentAccount = investmentAccount;
        this.charge = charge;
        this.amount = amount;
        this.penaltyCharge = penaltyCharge;
        this.status = status;
        this.inactivationDate = inactivationDate;
    }

    
    public InvestmentAccountCharge() {
        super();
    }


    public InvestmentAccount getInvestmentAccount() {
        return this.investmentAccount;
    }

    
    public void setInvestmentAccount(InvestmentAccount investmentAccount) {
        this.investmentAccount = investmentAccount;
    }

    
    public Charge getCharge() {
        return this.charge;
    }

    
    public void setCharge(Charge charge) {
        this.charge = charge;
    }

    
    public boolean isPenaltyCharge() {
        return this.penaltyCharge;
    }

    
    public void setPenaltyCharge(boolean penaltyCharge) {
        this.penaltyCharge = penaltyCharge;
    }

    
    public boolean isStatus() {
        return this.status;
    }

    
    public void setStatus(boolean status) {
        this.status = status;
    }

    
    public Date getInactivationDate() {
        return this.inactivationDate;
    }

    
    public void setInactivationDate(Date inactivationDate) {
        this.inactivationDate = inactivationDate;
    }


    
    public BigDecimal getAmount() {
        return this.amount;
    }

    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public boolean isExternalInvestmentCharge() {
        return this.charge.isExternalInvestmentCharge();
    }
    
    public boolean isAcivationCharge() {
        return this.charge.isInvestmentActivationCharge();
    }
    
    public boolean isMaturityCharge() {
        return this.charge.isMaturityCharge();
    }
    
}
