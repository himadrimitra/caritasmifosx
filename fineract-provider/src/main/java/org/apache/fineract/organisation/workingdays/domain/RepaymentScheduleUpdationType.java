package org.apache.fineract.organisation.workingdays.domain;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum RepaymentScheduleUpdationType {

    INVALID(0, "repaymentScheduleUpdationType.invalid"), //
    EXTENDREPAYMENTSCHEDULE(1, "extend.repayment.schedule"), //
    SCHEDULEONLYONDATE(2, "schedule.only.date");

    private final Integer value;
    private final String code;

    private RepaymentScheduleUpdationType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public boolean isExtendRepaymentSchedule() {
        return this.value.equals(RepaymentScheduleUpdationType.EXTENDREPAYMENTSCHEDULE.getValue());
    }
    
    public boolean isScheduleOnlyOnDate() {
        return this.value.equals(RepaymentScheduleUpdationType.SCHEDULEONLYONDATE.getValue());
    }
    
    public static RepaymentScheduleUpdationType fromInt(final Integer frequency) {
        RepaymentScheduleUpdationType repaymentScheduleUpdationType = RepaymentScheduleUpdationType.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case 1:
                    repaymentScheduleUpdationType = RepaymentScheduleUpdationType.EXTENDREPAYMENTSCHEDULE;
                break;
                case 2:
                    repaymentScheduleUpdationType = RepaymentScheduleUpdationType.SCHEDULEONLYONDATE;
                break;
            }
        }
        return repaymentScheduleUpdationType;
    }

    public static EnumOptionData repaymentScheduleUpdationTypeOptionData(final int id) {
        return repaymentScheduleUpdationType(RepaymentScheduleUpdationType.fromInt(id));
    }

    public static EnumOptionData repaymentScheduleUpdationType(final RepaymentScheduleUpdationType type) {
        EnumOptionData optionData = null;
        switch (type) {
            case EXTENDREPAYMENTSCHEDULE:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Extend Repayment Schedule");
            break;
            case SCHEDULEONLYONDATE:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Schedule Only On Date");
            break;
            default:
            break;
        }
        return optionData;
    }

}
