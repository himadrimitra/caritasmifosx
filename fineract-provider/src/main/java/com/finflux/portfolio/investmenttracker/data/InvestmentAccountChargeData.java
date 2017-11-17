package com.finflux.portfolio.investmenttracker.data;

import java.math.BigDecimal;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.joda.time.LocalDate;


public class InvestmentAccountChargeData {
    
    private Long chargeId;
    private Long investmentAccountId;
    private boolean isActive;
    private boolean isPenality;
    private LocalDate inactivationDate;
    private String chargeName;
    private BigDecimal chargeAmount;
    private EnumOptionData chargeTimeType;
    private EnumOptionData chargeCalculationType;
    
    public InvestmentAccountChargeData(Long chargeId, Long investmentAccountId, boolean isActive, boolean isPenality,
            LocalDate inactivationDate, String chargeName, BigDecimal chargeAmount, EnumOptionData chargeTimeType,
            EnumOptionData chargeCalculationType) {
        this.chargeId = chargeId;
        this.investmentAccountId = investmentAccountId;
        this.isActive = isActive;
        this.isPenality = isPenality;
        this.inactivationDate = inactivationDate;
        this.chargeName = chargeName;
        this.chargeAmount = chargeAmount;
        this.chargeTimeType = chargeTimeType;
        this.chargeCalculationType = chargeCalculationType;
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

    
    public String getChargeName() {
        return this.chargeName;
    }

    
    public BigDecimal getChargeAmount() {
        return this.chargeAmount;
    }

    
    public EnumOptionData getChargeTimeType() {
        return this.chargeTimeType;
    }

    
    public EnumOptionData getChargeCalculationType() {
        return this.chargeCalculationType;
    }

}
