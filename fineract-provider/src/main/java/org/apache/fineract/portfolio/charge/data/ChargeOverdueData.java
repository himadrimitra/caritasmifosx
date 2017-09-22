package org.apache.fineract.portfolio.charge.data;

import java.math.BigDecimal;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class ChargeOverdueData {

    @SuppressWarnings("unused")
    private final Long id;
    private final Integer gracePeriod;
    private final Integer penaltyFreePeriod;
    private final EnumOptionData graceType;
    private final boolean applyChargeForBrokenPeriod;
    private final boolean isBasedOnOriginalSchedule;
    private final boolean considerOnlyPostedInterest;
    private final boolean calculateChargeOnCurrentOverdue;
    private final BigDecimal minOverdueAmountRequired;
    private final boolean stopChargeOnNPA;

    public ChargeOverdueData(final Long id, final Integer gracePeriod, final Integer penaltyFreePeriod, final EnumOptionData graceType,
            final boolean applyChargeForBrokenPeriod, final boolean isBasedOnOriginalSchedule, final boolean considerOnlyPostedInterest,
            final boolean calculateChargeOnCurrentOverdue, final boolean stopChargeOnNPA, final BigDecimal minOverdueAmountRequired) {
        this.id = id;
        this.gracePeriod = gracePeriod;
        this.penaltyFreePeriod = penaltyFreePeriod;
        this.graceType = graceType;
        this.applyChargeForBrokenPeriod = applyChargeForBrokenPeriod;
        this.isBasedOnOriginalSchedule = isBasedOnOriginalSchedule;
        this.considerOnlyPostedInterest = considerOnlyPostedInterest;
        this.calculateChargeOnCurrentOverdue = calculateChargeOnCurrentOverdue;
        this.stopChargeOnNPA = stopChargeOnNPA;
        this.minOverdueAmountRequired = minOverdueAmountRequired;
    }

    public Integer getGracePeriod() {
        return this.gracePeriod;
    }

    public Integer getPenaltyFreePeriod() {
        return this.penaltyFreePeriod;
    }

    public Integer getGraceType() {
        return this.graceType.getId().intValue();
    }

    public boolean isApplyChargeForBrokenPeriod() {
        return this.applyChargeForBrokenPeriod;
    }

    public boolean isBasedOnOriginalSchedule() {
        return this.isBasedOnOriginalSchedule;
    }

    public boolean isConsiderOnlyPostedInterest() {
        return this.considerOnlyPostedInterest;
    }

    public boolean isCalculateChargeOnCurrentOverdue() {
        return this.calculateChargeOnCurrentOverdue;
    }

    
    public BigDecimal getMinOverdueAmountRequired() {
        return this.minOverdueAmountRequired;
    }

    public boolean isStopChargeOnNPA() {
        return stopChargeOnNPA;
    }

}
