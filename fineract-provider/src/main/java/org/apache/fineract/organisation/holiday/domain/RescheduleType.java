package org.apache.fineract.organisation.holiday.domain;

public enum RescheduleType {

	INVALID(0, "rescheduletype.invalid"), RESCHEDULETOSPECIFICDATE(2, "rescheduletype.rescheduletospecificdate"), //
	RESCHEDULETONEXTREPAYMENTDATE(1, "rescheduletype.rescheduletonextrepaymentdate");

	private final Integer value;
	private final String code;

	private RescheduleType(Integer value, String code) {
		this.value = value;
		this.code = code;
	}

	public static RescheduleType fromInt(int rescheduleTypeValue) {
		RescheduleType enumerration = RescheduleType.INVALID;
		switch (rescheduleTypeValue) {
		case 1:
			enumerration = RescheduleType.RESCHEDULETONEXTREPAYMENTDATE;
			break;
		case 2:
			enumerration = RescheduleType.RESCHEDULETOSPECIFICDATE;
			break;
		}
		return enumerration;
	}

	public boolean isRescheduleToSpecificDate(){
		return this.value.equals(RescheduleType.RESCHEDULETOSPECIFICDATE.getValue());
	}
	
	public boolean isResheduleToNextRepaymentDate(){
		return this.value.equals(RescheduleType.RESCHEDULETONEXTREPAYMENTDATE.getValue());
	}
	
	public Integer getValue() {
		return this.value;
	}

	public String getCode() {
		return this.code;
	}
	
}