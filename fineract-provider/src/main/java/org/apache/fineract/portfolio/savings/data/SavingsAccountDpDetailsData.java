package org.apache.fineract.portfolio.savings.data;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.fineract.portfolio.savings.SavingsDpLimitCalculationType;
import org.apache.fineract.portfolio.savings.SavingsDpLimitFrequencyType;


public class SavingsAccountDpDetailsData {
    
    private final Long id;
    private final Long savingsAccountId;
    private final SavingsDpLimitFrequencyType frequencyType;
    private final Integer dpReductionEvery;
    private final Integer duration;
    private final BigDecimal amount;
    private final BigDecimal dpAmount;
    private final SavingsDpLimitCalculationType calculationType;
    private final BigDecimal amountOrPercentage;
    private final Date savingsActivatedonDate;
    
    public SavingsAccountDpDetailsData(Long id, Long savingsAccountId, SavingsDpLimitFrequencyType frequencyType, Integer dpReductionEvery, Integer duration, BigDecimal amount,
            BigDecimal dpAmount, SavingsDpLimitCalculationType calculationType, BigDecimal amountOrPercentage, Date savingsActivatedonDate) {
        super();
        this.id = id;
        this.savingsAccountId = savingsAccountId;
        this.frequencyType = frequencyType;
        this.dpReductionEvery = dpReductionEvery;
        this.duration = duration;
        this.amount = amount;
        this.dpAmount = dpAmount;
        this.calculationType = calculationType;
        this.amountOrPercentage = amountOrPercentage;
        this.savingsActivatedonDate = savingsActivatedonDate;
    }
    
    public static SavingsAccountDpDetailsData createNew(Long id, Long savingsAccountId, SavingsDpLimitFrequencyType frequencyType, Integer dpReductionEvery, Integer duration, BigDecimal amount,
            BigDecimal dpAmount, SavingsDpLimitCalculationType calculationType, BigDecimal amountOrPercentage, Date savingsActivatedonDate) {
        return new SavingsAccountDpDetailsData(id, savingsAccountId, frequencyType, dpReductionEvery, duration, amount, dpAmount, calculationType , amountOrPercentage,
                savingsActivatedonDate);
    }
    
    public Date getsavingsActivatedonDate() {
        return this.savingsActivatedonDate;
    }

    
    public Long getId() {
        return this.id;
    }

    
    public SavingsDpLimitFrequencyType getFrequencyType() {
        return this.frequencyType;
    }

    
    public Integer getDpReductionEvery() {
        return this.dpReductionEvery;
    }

    
    public Integer getDuration() {
        return this.duration;
    }

    
    public BigDecimal getAmount() {
        return this.amount;
    }

    
    public BigDecimal getDpAmount() {
        return this.dpAmount;
    }

    
    public SavingsDpLimitCalculationType getCalculationType() {
        return this.calculationType;
    }

    
    public BigDecimal getAmountOrPercentage() {
        return this.amountOrPercentage;
    }

    
    public Long getSavingsAccountId() {
        return this.savingsAccountId;
    }
    
}
