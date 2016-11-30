package org.apache.fineract.portfolio.cgt.domain;

public enum CgtDayStatusType {

    NEW(1, "CgtStatusType.new"), COMPLETE(2, "CgtStatusType.complete");

    private final Integer value;
    private final String code;

    public static CgtDayStatusType fromInt(final Integer statusValue) {

        CgtDayStatusType enumeration = CgtDayStatusType.NEW;
        switch (statusValue) {
            case 2:
                enumeration = CgtDayStatusType.COMPLETE;
            break;
        }
        return enumeration;
    }

    private CgtDayStatusType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public boolean isNew() {
        return this.value.equals(CgtDayStatusType.NEW.getValue());
    }

    public boolean isComplete() {
        return this.value.equals(CgtDayStatusType.COMPLETE.getValue());
    }

}
