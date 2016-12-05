package org.apache.fineract.portfolio.savings.domain;

import java.math.BigDecimal;
import java.math.MathContext;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.fineract.organisation.monetary.domain.MoneyHelper;
import org.apache.fineract.portfolio.savings.SavingsDpLimitCalculationType;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Table(name = "f_savings_account_dp_details")
public class SavingsAccountDpDetails extends AbstractPersistable<Long> {
    
    @OneToOne
    @JoinColumn(name = "savings_id", nullable = false)
    SavingsAccount savingsAccount;
    
    @Column(name = "frequency", nullable = false)
    private Integer frequencyType;

    @Column(name = "dp_reduction_every", nullable = false)
    private Integer dpReductionEvery;

    @Column(name = "duration", nullable = false)
    private Integer duration;
    
    @Column(name = "amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal amount;
    
    @Column(name = "dp_amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal dpAmount;
    
    @Column(name = "calculation_type", scale = 6, precision = 19, nullable = false)
    private Integer calculationType;
    
    @Column(name = "amount_or_percentage", scale = 6, precision = 19, nullable = false)
    private BigDecimal amountOrPercentage;
    
    protected SavingsAccountDpDetails() {
        //
    }
    
    public static SavingsAccountDpDetails createNew(final SavingsAccount savingsAccount, final Integer frequencyType, final Integer dpReductionEvery, Integer duration, 
            final BigDecimal dpAmount, final Integer calculationType, final BigDecimal amountOrPercentage) {
        return new SavingsAccountDpDetails(savingsAccount, frequencyType, dpReductionEvery, duration, dpAmount, calculationType, amountOrPercentage);
        
    }
    
    private SavingsAccountDpDetails(final SavingsAccount savingsAccount, final Integer frequencyType, final Integer dpReductionEvery, Integer duration,
            final BigDecimal dpAmount, final Integer calculationType, final BigDecimal amountOrPercentage) {
        this.savingsAccount = savingsAccount;
        this.frequencyType = frequencyType;
        this.dpReductionEvery = dpReductionEvery;
        this.duration = duration;
        this.dpAmount = dpAmount;
        this.calculationType = calculationType;
        this.amountOrPercentage = amountOrPercentage;
        this.amount = populateDerivedFields(calculationType, amountOrPercentage, dpAmount);
    }
    
    
    public BigDecimal populateDerivedFields(Integer calculationType, BigDecimal amountOrPersentage, BigDecimal dpLimitAmount) {
        BigDecimal amount = BigDecimal.ZERO;
        switch (SavingsDpLimitCalculationType.fromInt(calculationType)) {
            case FLAT:
                amount = amountOrPersentage;
            break;
            case PERCENT_OF_AMOUNT:
                amount = percentageOf(dpLimitAmount, amountOrPersentage);
            break;
            default:
            break;
        }
        return amount;
    }
    
    public static BigDecimal percentageOf(final BigDecimal value, final BigDecimal percentage) {

        BigDecimal percentageOf = BigDecimal.ZERO;

        if (value.compareTo(BigDecimal.ZERO) > 0) {
            final MathContext mc = new MathContext(8, MoneyHelper.getRoundingMode());
            final BigDecimal multiplicand = percentage.divide(BigDecimal.valueOf(100l), mc);
            percentageOf = value.multiply(multiplicand, mc);
        }
        return percentageOf;
    }

    
    public SavingsAccount getSavingsAccount() {
        return this.savingsAccount;
    }

    
    public Integer getFrequencyType() {
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

    
    public Integer getCalculationType() {
        return this.calculationType;
    }

    
    public BigDecimal getAmountOrPercentage() {
        return this.amountOrPercentage;
    }

    
    public void setSavingsAccount(SavingsAccount savingsAccount) {
        this.savingsAccount = savingsAccount;
    }

    
    public void setFrequencyType(Integer frequencyType) {
        this.frequencyType = frequencyType;
    }

    
    public void setDpReductionEvery(Integer dpReductionEvery) {
        this.dpReductionEvery = dpReductionEvery;
    }

    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    
    public void setDpAmount(BigDecimal dpAmount) {
        this.dpAmount = dpAmount;
    }

    
    public void setCalculationType(Integer calculationType) {
        this.calculationType = calculationType;
    }

    
    public void setAmountOrPercentage(BigDecimal amountOrPercentage) {
        this.amountOrPercentage = amountOrPercentage;
    }
    
    
}
