package org.apache.fineract.portfolio.cgt.domain;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum CgtStatusType {

    NEW(1, "CgtStatusType.new"), IN_PROGRESS(2, "CgtStatusType.in.progress"), COMPLETE(3, "CgtStatusType.complete"), REJECT(4,
            "CgtStatusType.reject");

    private final Integer value;
    private final String code;

    public static CgtStatusType fromInt(final Integer statusValue) {

        CgtStatusType enumeration = CgtStatusType.NEW;
        switch (statusValue) {
            case 2:
                enumeration = CgtStatusType.IN_PROGRESS;
            break;
            case 3:
                enumeration = CgtStatusType.COMPLETE;
            break;
            case 4:
                enumeration = CgtStatusType.REJECT;
            break;
        }
        return enumeration;
    }

    public static EnumOptionData CgtStatusTypeEnumDatafromInt(final Integer statusValue) {
        EnumOptionData optionData = null;
        switch (statusValue) {
            case 1:
                optionData = new EnumOptionData(CgtStatusType.NEW.getValue().longValue(), CgtStatusType.NEW.getCode(), "NEW");
            break;
            case 2:
                optionData = new EnumOptionData(CgtStatusType.IN_PROGRESS.getValue().longValue(), CgtStatusType.IN_PROGRESS.getCode(),
                        "IN PROGRESS");
            break;
            case 3:
                optionData = new EnumOptionData(CgtStatusType.COMPLETE.getValue().longValue(), CgtStatusType.COMPLETE.getCode(), "COMPLETE");
            break;
            case 4:
                optionData = new EnumOptionData(CgtStatusType.REJECT.getValue().longValue(), CgtStatusType.REJECT.getCode(), "REJECT");
            break;

        }
        return optionData;
    }

    private CgtStatusType(final Integer value, final String code) {
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
        return this.value.equals(CgtStatusType.NEW.getValue());
    }

    public boolean isInProgrss() {
        return this.value.equals(CgtStatusType.IN_PROGRESS.getValue());
    }

    public boolean isComplete() {
        return this.value.equals(CgtStatusType.COMPLETE.getValue());
    }

    public boolean isRejected() {
        return this.value.equals(CgtStatusType.REJECT.getValue());
    }
}
