package org.apache.fineract.organisation.workingdays.domain;

public enum RepaymentScheduleUpdationType {
	
	EXTENDREPAYMENTSCHEDULE(1,"extend.repayment.schedule"),SCHEDULEONLYONDATE(2,"schedule.only.date");
	
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


}
