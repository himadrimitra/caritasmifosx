package org.apache.fineract.portfolio.savings.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class SavingsProductDrawingPowerDetailsData {

    private final EnumOptionData frequencyType;
    private final Integer frequencyInterval;
    private final EnumOptionData frequencyNthDay;
    private final EnumOptionData frequencyDayOfWeekType;
    private final Integer frequencyOnDay;

    public SavingsProductDrawingPowerDetailsData(final EnumOptionData frequencyType, final Integer frequencyInterval,
            final EnumOptionData frequencyNthDay, final EnumOptionData frequencyDayOfWeekType, final Integer frequencyOnDay) {
        this.frequencyType = frequencyType;
        this.frequencyInterval = frequencyInterval;
        this.frequencyNthDay = frequencyNthDay;
        this.frequencyDayOfWeekType = frequencyDayOfWeekType;
        this.frequencyOnDay = frequencyOnDay;
    }

    public static SavingsProductDrawingPowerDetailsData createNew(final EnumOptionData frequencyType, final Integer frequencyInterval,
            final EnumOptionData frequencyNthDay, final EnumOptionData frequencyDayOfWeekType, final Integer frequencyOnDay) {
        return new SavingsProductDrawingPowerDetailsData(frequencyType, frequencyInterval, frequencyNthDay, frequencyDayOfWeekType,
                frequencyOnDay);
    }

    public EnumOptionData getFrequencyType() {
        return this.frequencyType;
    }

    public Integer getFrequencyInterval() {
        return this.frequencyInterval;
    }

    public EnumOptionData getFrequencyNthDay() {
        return this.frequencyNthDay;
    }

    public EnumOptionData getFrequencyDayOfWeekType() {
        return this.frequencyDayOfWeekType;
    }

    public Integer getFrequencyOnDay() {
        return this.frequencyOnDay;
    }
}