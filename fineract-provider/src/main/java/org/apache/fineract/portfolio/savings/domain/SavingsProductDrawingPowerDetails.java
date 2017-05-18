package org.apache.fineract.portfolio.savings.domain;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.common.domain.DayOfWeekType;
import org.apache.fineract.portfolio.common.domain.NthDayType;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.savings.SavingsApiConstants;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_savings_product_drawing_power_details")
public class SavingsProductDrawingPowerDetails extends AbstractPersistable<Long> {

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private SavingsProduct savingsProduct;

    /**
     * {@link PeriodFrequencyType}
     */
    @Column(name = "frequency_type_enum", nullable = false)
    private Integer frequencyType;

    @Column(name = "frequency_interval", nullable = false)
    private Integer frequencyInterval;

    /**
     * {@link NthDayType}
     */
    @Column(name = "frequency_nth_day_enum", nullable = false)
    private Integer frequencyNthDay;

    /**
     * {@link DayOfWeekType}
     */
    @Column(name = "frequency_day_of_week_type_enum", nullable = false)
    private Integer frequencyDayOfWeekType;

    @Column(name = "frequency_on_day", nullable = true)
    private Integer frequencyOnDay;

    protected SavingsProductDrawingPowerDetails() {
        //
    }

    public static SavingsProductDrawingPowerDetails create(final SavingsProduct savingsProduct, final Integer frequencyType,
            final Integer frequencyInterval, final Integer frequencyNthDay, final Integer frequencyDayOfWeekType,
            final Integer frequencyOnDay) {
        return new SavingsProductDrawingPowerDetails(savingsProduct, frequencyType, frequencyInterval, frequencyNthDay,
                frequencyDayOfWeekType, frequencyOnDay);
    }

    private SavingsProductDrawingPowerDetails(final SavingsProduct savingsProduct, final Integer frequencyType,
            final Integer frequencyInterval, final Integer frequencyNthDay, final Integer frequencyDayOfWeekType,
            final Integer frequencyOnDay) {
        this.savingsProduct = savingsProduct;
        this.frequencyType = frequencyType;
        this.frequencyInterval = frequencyInterval;
        this.frequencyNthDay = frequencyNthDay;
        this.frequencyDayOfWeekType = frequencyDayOfWeekType;
        this.frequencyOnDay = frequencyOnDay;
    }

    public Map<String, Object> update(final SavingsProduct savingsProduct, final JsonCommand command,
            final Map<String, Object> actualChanges) {
        this.savingsProduct = savingsProduct;
        if (command.isChangeInIntegerParameterNamed(SavingsApiConstants.dpFrequencyTypeParamName, this.frequencyType)) {
            final Integer newValue = command.integerValueOfParameterNamed(SavingsApiConstants.dpFrequencyTypeParamName);
            actualChanges.put(SavingsApiConstants.dpFrequencyTypeParamName, newValue);
            this.frequencyType = newValue;
        }
        if (command.isChangeInIntegerParameterNamed(SavingsApiConstants.dpFrequencyIntervalParamName, this.frequencyInterval)) {
            final Integer newValue = command.integerValueOfParameterNamed(SavingsApiConstants.dpFrequencyIntervalParamName);
            actualChanges.put(SavingsApiConstants.dpFrequencyIntervalParamName, newValue);
            this.frequencyInterval = newValue;
        }
        if (command.isChangeInIntegerParameterNamed(SavingsApiConstants.dpFrequencyNthDayParamName, this.frequencyNthDay)) {
            final Integer newValue = command.integerValueOfParameterNamed(SavingsApiConstants.dpFrequencyNthDayParamName);
            actualChanges.put(SavingsApiConstants.dpFrequencyNthDayParamName, newValue);
            this.frequencyNthDay = newValue;
        }
        if (command.isChangeInIntegerParameterNamed(SavingsApiConstants.dpFrequencyDayOfWeekTypeParamName, this.frequencyDayOfWeekType)) {
            final Integer newValue = command.integerValueOfParameterNamed(SavingsApiConstants.dpFrequencyDayOfWeekTypeParamName);
            actualChanges.put(SavingsApiConstants.dpFrequencyDayOfWeekTypeParamName, newValue);
            this.frequencyDayOfWeekType = newValue;
        }
        if (command.isChangeInIntegerParameterNamed(SavingsApiConstants.dpFrequencyOnDayParamName, this.frequencyOnDay)) {
            final Integer newValue = command.integerValueOfParameterNamed(SavingsApiConstants.dpFrequencyOnDayParamName);
            actualChanges.put(SavingsApiConstants.dpFrequencyOnDayParamName, newValue);
            this.frequencyOnDay = newValue;
        }
        return actualChanges;
    }

    public SavingsProduct getSavingsProduct() {
        return this.savingsProduct;
    }

    public Integer getFrequencyType() {
        return this.frequencyType;
    }

    public Integer getFrequencyInterval() {
        return this.frequencyInterval;
    }

    public Integer getFrequencyNthDay() {
        return this.frequencyNthDay;
    }

    public Integer getFrequencyDayOfWeekType() {
        return this.frequencyDayOfWeekType;
    }

    public Integer getFrequencyOnDay() {
        return this.frequencyOnDay;
    }
}