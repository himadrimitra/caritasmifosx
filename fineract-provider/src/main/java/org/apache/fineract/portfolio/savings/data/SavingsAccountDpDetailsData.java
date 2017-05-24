package org.apache.fineract.portfolio.savings.data;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class SavingsAccountDpDetailsData {

    private final Long id;
    private final Long savingsAccountId;
    private final Integer duration;
    private final BigDecimal amount;
    private final BigDecimal dpAmount;
    private final EnumOptionData calculationType;
    private final BigDecimal amountOrPercentage;
    private final Date savingsActivatedonDate;
    private final Date startDate;
    private final SavingsProductDrawingPowerDetailsData savingsProductDrawingPowerDetailsData;

    public SavingsAccountDpDetailsData(final Long id, final Long savingsAccountId, final Integer duration, final BigDecimal amount,
            final BigDecimal dpAmount, final EnumOptionData calculationType, final BigDecimal amountOrPercentage,
            final Date savingsActivatedonDate, final Date startDate,
            final SavingsProductDrawingPowerDetailsData savingsProductDrawingPowerDetailsData) {
        super();
        this.id = id;
        this.savingsAccountId = savingsAccountId;
        this.duration = duration;
        this.amount = amount;
        this.dpAmount = dpAmount;
        this.calculationType = calculationType;
        this.amountOrPercentage = amountOrPercentage;
        this.savingsActivatedonDate = savingsActivatedonDate;
        this.startDate = startDate;
        this.savingsProductDrawingPowerDetailsData = savingsProductDrawingPowerDetailsData;
    }

    public static SavingsAccountDpDetailsData createNew(final Long id, final Long savingsAccountId, final Integer duration,
            final BigDecimal amount, final BigDecimal dpAmount, final EnumOptionData calculationType, final BigDecimal amountOrPercentage,
            final Date savingsActivatedonDate, final Date startDate,
            final SavingsProductDrawingPowerDetailsData savingsProductDrawingPowerDetailsData) {
        return new SavingsAccountDpDetailsData(id, savingsAccountId, duration, amount, dpAmount, calculationType, amountOrPercentage,
                savingsActivatedonDate, startDate, savingsProductDrawingPowerDetailsData);
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public Long getId() {
        return this.id;
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

    public EnumOptionData getCalculationType() {
        return this.calculationType;
    }

    public BigDecimal getAmountOrPercentage() {
        return this.amountOrPercentage;
    }

    public Long getSavingsAccountId() {
        return this.savingsAccountId;
    }

    public Date getSavingsActivatedonDate() {
        return this.savingsActivatedonDate;
    }

    public SavingsProductDrawingPowerDetailsData getSavingsProductDrawingPowerDetailsData() {
        return this.savingsProductDrawingPowerDetailsData;
    }

}