package org.apache.fineract.portfolio.cgt.domain;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum CgtDayClientAttendanceStatusType {

    PRESENT(1, "CgtDayClientAttendanceStatusType.present"), ABSENT(2, "CgtDayClientAttendanceStatusType.absent");

    private final Integer value;
    private final String code;

    public static CgtDayClientAttendanceStatusType fromInt(final Integer statusValue) {

        CgtDayClientAttendanceStatusType enumeration = CgtDayClientAttendanceStatusType.PRESENT;
        switch (statusValue) {
            case 2:
                enumeration = CgtDayClientAttendanceStatusType.ABSENT;
            break;
        }
        return enumeration;
    }

    public static EnumOptionData CgtDayClientAttendanceStatusTypeEnumDatafromInt(final Integer statusValue) {
        EnumOptionData optionData = null;
        switch (statusValue) {
            case 1:
                optionData = new EnumOptionData(CgtDayClientAttendanceStatusType.PRESENT.getValue().longValue(),
                        CgtDayClientAttendanceStatusType.PRESENT.getCode(), "PRESENT");
            break;
            case 2:
                optionData = new EnumOptionData(CgtDayClientAttendanceStatusType.ABSENT.getValue().longValue(),
                        CgtDayClientAttendanceStatusType.ABSENT.getCode(), "ABSENT");
            break;
        }
        return optionData;
    }

    private CgtDayClientAttendanceStatusType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public boolean isPresent() {
        return this.value.equals(CgtDayClientAttendanceStatusType.PRESENT.getValue());
    }

    public boolean isAbsent() {
        return this.value.equals(CgtDayClientAttendanceStatusType.ABSENT.getValue());
    }

}
