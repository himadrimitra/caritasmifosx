package com.finflux.portfolio.investmenttracker.data;

import org.joda.time.LocalDate;


public class InvestmentAccountChargeData {
    
    private Long chargeId;
    private Long investmentAccountId;
    private boolean isActive;
    private boolean isPenality;
    private LocalDate inactivationDate;
    
    public InvestmentAccountChargeData(Long chargeId, Long investmentAccountId, boolean isActive, boolean isPenality,
            LocalDate inactivationDate) {
        this.chargeId = chargeId;
        this.investmentAccountId = investmentAccountId;
        this.isActive = isActive;
        this.isPenality = isPenality;
        this.inactivationDate = inactivationDate;
    }

    
    public Long getChargeId() {
        return this.chargeId;
    }

    
    public void setChargeId(Long chargeId) {
        this.chargeId = chargeId;
    }

    
    public Long getInvestmentAccountId() {
        return this.investmentAccountId;
    }

    
    public void setInvestmentAccountId(Long investmentAccountId) {
        this.investmentAccountId = investmentAccountId;
    }

    
    public boolean isActive() {
        return this.isActive;
    }

    
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    
    public boolean isPenality() {
        return this.isPenality;
    }

    
    public void setPenality(boolean isPenality) {
        this.isPenality = isPenality;
    }

    
    public LocalDate getInactivationDate() {
        return this.inactivationDate;
    }

    
    public void setInactivationDate(LocalDate inactivationDate) {
        this.inactivationDate = inactivationDate;
    }
 

}
