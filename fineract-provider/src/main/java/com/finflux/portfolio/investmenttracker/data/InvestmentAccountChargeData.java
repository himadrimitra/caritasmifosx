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
    private BigDecimal amountOrPercentage;
    private EnumOptionData chargeTimeType;
    private EnumOptionData chargeCalculationType;
    private BigDecimal amount;
    
    public InvestmentAccountChargeData(final Long chargeId, final Long investmentAccountId, final boolean isActive, final boolean isPenality,
            final LocalDate inactivationDate, final String chargeName, final BigDecimal amountOrPercentage, final EnumOptionData chargeTimeType,
            final EnumOptionData chargeCalculationType, final BigDecimal amount) {
        this.chargeId = chargeId;
        this.investmentAccountId = investmentAccountId;
        this.isActive = isActive;
        this.isPenality = isPenality;
        this.inactivationDate = inactivationDate;
        this.chargeName = chargeName;
        this.amountOrPercentage = amountOrPercentage;
        this.chargeTimeType = chargeTimeType;
        this.chargeCalculationType = chargeCalculationType;
        this.amount = amount;
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
        return this.amountOrPercentage;
    }

    
    public EnumOptionData getChargeTimeType() {
        return this.chargeTimeType;
    }

    
    public EnumOptionData getChargeCalculationType() {
        return this.chargeCalculationType;
    }

    
    public BigDecimal getAmount() {
        return this.amount;
    }

    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    

}
