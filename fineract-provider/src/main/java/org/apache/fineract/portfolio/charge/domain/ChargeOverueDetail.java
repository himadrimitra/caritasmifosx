package org.apache.fineract.portfolio.charge.domain;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.charge.api.ChargesApiConstants;
import org.apache.fineract.portfolio.charge.data.ChargeOverdueData;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_charge_overdue_detail")
public class ChargeOverueDetail extends AbstractPersistable<Long> {

    @OneToOne
    @JoinColumn(name = "charge_id")
    private Charge charge;

    @Column(name = "grace_period", nullable = false)
    private Integer gracePeriod;

    @Column(name = "penalty_free_period", nullable = false)
    private Integer penaltyFreePeriod;

    @Column(name = "grace_type_enum", nullable = false)
    private Integer graceType;

    @Column(name = "apply_charge_for_broken_period", nullable = false)
    private boolean applyChargeForBrokenPeriod;

    @Column(name = "is_based_on_original_schedule", nullable = false)
    private boolean isBasedOnOriginalSchedule;

    @Column(name = "consider_only_posted_interest", nullable = false)
    private boolean considerOnlyPostedInterest;

    @Column(name = "calculate_charge_on_current_overdue", nullable = false)
    private boolean calculateChargeOnCurrentOverdue;
    
    @Column(name = "stop_charge_on_npa", nullable = false)
    private boolean stopChargeOnNPA;
    
    @Column(name = "min_overdue_amount_required")
    private BigDecimal minOverdueAmountRequired;

    protected ChargeOverueDetail() {
        // TODO Auto-generated constructor stub
    }

    private ChargeOverueDetail(final Integer gracePeriod, final Integer penaltyFreePeriod, final PenaltyGraceType penaltyGraceType,
            final boolean applyChargeForBrokenPeriod, final boolean isBasedOnOriginalSchedule, final boolean considerOnlyPostedInterest,
            final boolean calculateChargeOnCurrentOverdue, final BigDecimal minOverdueAmountRequired, final boolean stopOverdueChargesOnNPA) {
        this.gracePeriod = gracePeriod;
        this.penaltyFreePeriod = penaltyFreePeriod;
        this.graceType = penaltyGraceType.getValue();
        this.applyChargeForBrokenPeriod = applyChargeForBrokenPeriod;
        this.isBasedOnOriginalSchedule = isBasedOnOriginalSchedule;
        this.considerOnlyPostedInterest = considerOnlyPostedInterest;
        this.calculateChargeOnCurrentOverdue = calculateChargeOnCurrentOverdue;
        this.minOverdueAmountRequired = minOverdueAmountRequired;
        this.stopChargeOnNPA = stopOverdueChargesOnNPA;
    }

    public static ChargeOverueDetail fromJson(final JsonCommand command, final Locale locale) {

        Integer gracePeriod = 0;
        if (command.hasParameter(ChargesApiConstants.gracePeriodParamName)) {
            gracePeriod = command.integerValueOfParameterNamed(ChargesApiConstants.gracePeriodParamName, locale);
        }
        Integer penaltyFreePeriod = 0;
        if (command.hasParameter(ChargesApiConstants.penaltyFreePeriodParamName)) {
            penaltyFreePeriod = command.integerValueOfParameterNamed(ChargesApiConstants.penaltyFreePeriodParamName, locale);
        }
        PenaltyGraceType penaltyGraceType = PenaltyGraceType.EACH_OVERDUE_INSTALLEMNT;
        if (command.hasParameter(ChargesApiConstants.graceTypeParamName)) {
            penaltyGraceType = PenaltyGraceType.fromInt(command
                    .integerValueOfParameterNamed(ChargesApiConstants.graceTypeParamName, locale));
        }
        final boolean applyChargeForBrokenPeriod = command
                .booleanPrimitiveValueOfParameterNamed(ChargesApiConstants.applyChargeForBrokenPeriodParamName);
        final boolean isBasedOnOriginalSchedule = command
                .booleanPrimitiveValueOfParameterNamed(ChargesApiConstants.isBasedOnOriginalScheduleParamName);
        final boolean considerOnlyPostedInterest = command
                .booleanPrimitiveValueOfParameterNamed(ChargesApiConstants.considerOnlyPostedInterestParamName);
        final boolean calculateChargeOnCurrentOverdue = command
                .booleanPrimitiveValueOfParameterNamed(ChargesApiConstants.calculateChargeOnCurrentOverdueParamName);
        final boolean stopOverdueChargesOnNPA = command
                .booleanPrimitiveValueOfParameterNamed(ChargesApiConstants.stopChargeOnNPAParamName);
        final BigDecimal minOverdueAmountRequired = command.bigDecimalValueOfParameterNamed(ChargesApiConstants.minOverdueAmountRequiredParamName,locale);

        return new ChargeOverueDetail(gracePeriod, penaltyFreePeriod, penaltyGraceType, applyChargeForBrokenPeriod,
                isBasedOnOriginalSchedule, considerOnlyPostedInterest, calculateChargeOnCurrentOverdue, minOverdueAmountRequired,
                stopOverdueChargesOnNPA);
    }

    public void update(final JsonCommand command, final Map<String, Object> actualChanges, final Locale locale) {

        if (command.isChangeInIntegerParameterNamed(ChargesApiConstants.gracePeriodParamName, this.gracePeriod, locale)) {
            final Integer newValue = command.integerValueOfParameterNamed(ChargesApiConstants.gracePeriodParamName, locale);
            actualChanges.put(ChargesApiConstants.gracePeriodParamName, newValue);
            this.gracePeriod = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(ChargesApiConstants.penaltyFreePeriodParamName, this.penaltyFreePeriod, locale)) {
            final Integer newValue = command.integerValueOfParameterNamed(ChargesApiConstants.penaltyFreePeriodParamName, locale);
            actualChanges.put(ChargesApiConstants.penaltyFreePeriodParamName, newValue);
            this.penaltyFreePeriod = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(ChargesApiConstants.graceTypeParamName, this.graceType, locale)) {
            final Integer newValue = command.integerValueOfParameterNamed(ChargesApiConstants.graceTypeParamName, locale);
            final PenaltyGraceType penaltyGraceType = PenaltyGraceType.fromInt(newValue);
            actualChanges.put(ChargesApiConstants.graceTypeParamName, penaltyGraceType.getValue());
            this.graceType = penaltyGraceType.getValue();
        }

        if (command.isChangeInBooleanParameterNamed(ChargesApiConstants.applyChargeForBrokenPeriodParamName,
                this.applyChargeForBrokenPeriod)) {
            final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(ChargesApiConstants.applyChargeForBrokenPeriodParamName);
            actualChanges.put(ChargesApiConstants.applyChargeForBrokenPeriodParamName, newValue);
            this.applyChargeForBrokenPeriod = newValue;
        }

        if (command.isChangeInBooleanParameterNamed(ChargesApiConstants.isBasedOnOriginalScheduleParamName, this.isBasedOnOriginalSchedule)) {
            final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(ChargesApiConstants.isBasedOnOriginalScheduleParamName);
            actualChanges.put(ChargesApiConstants.isBasedOnOriginalScheduleParamName, newValue);
            this.isBasedOnOriginalSchedule = newValue;
        }

        if (command.isChangeInBooleanParameterNamed(ChargesApiConstants.considerOnlyPostedInterestParamName,
                this.considerOnlyPostedInterest)) {
            final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(ChargesApiConstants.considerOnlyPostedInterestParamName);
            actualChanges.put(ChargesApiConstants.considerOnlyPostedInterestParamName, newValue);
            this.considerOnlyPostedInterest = newValue;
        }

        if (command.isChangeInBooleanParameterNamed(ChargesApiConstants.calculateChargeOnCurrentOverdueParamName,
                this.calculateChargeOnCurrentOverdue)) {
            final boolean newValue = command
                    .booleanPrimitiveValueOfParameterNamed(ChargesApiConstants.calculateChargeOnCurrentOverdueParamName);
            actualChanges.put(ChargesApiConstants.calculateChargeOnCurrentOverdueParamName, newValue);
            this.calculateChargeOnCurrentOverdue = newValue;
        }
        
        if (command.isChangeInBooleanParameterNamed(ChargesApiConstants.stopChargeOnNPAParamName, this.stopChargeOnNPA)) {
            final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(ChargesApiConstants.stopChargeOnNPAParamName);
            actualChanges.put(ChargesApiConstants.stopChargeOnNPAParamName, newValue);
            this.stopChargeOnNPA = newValue;
        }
        
        if (command.isChangeInBigDecimalParameterNamed(ChargesApiConstants.minOverdueAmountRequiredParamName, this.minOverdueAmountRequired,locale)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ChargesApiConstants.minOverdueAmountRequiredParamName,locale);
            actualChanges.put(ChargesApiConstants.minOverdueAmountRequiredParamName, newValue);
            this.minOverdueAmountRequired = newValue;
        }
    }

    public ChargeOverdueData toData() {
        EnumOptionData graceType = PenaltyGraceType.penaltyGraceType(this.graceType);
        return new ChargeOverdueData(getId(), gracePeriod, penaltyFreePeriod, graceType, applyChargeForBrokenPeriod,
                isBasedOnOriginalSchedule, considerOnlyPostedInterest, calculateChargeOnCurrentOverdue, stopChargeOnNPA,
                minOverdueAmountRequired);
    }

    public void setCharge(Charge charge) {
        this.charge = charge;
    }

    public Integer getGracePeriod() {
        return this.gracePeriod;
    }

    public Integer getPenaltyFreePeriod() {
        return this.penaltyFreePeriod;
    }

    public Integer getGraceType() {
        return this.graceType;
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

}