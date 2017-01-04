package com.finflux.risk.profilerating.data;

public enum ProfileRatingRunStatus {

    INVALID(0, "profileRatingRunStatus.invalid"), //
    INITIATED(1, "profileRatingRunStatus.group"), //
    INPROGRESS(2, "profileRatingRunStatus.center"), //
    COMPLETED(3, "profileRatingRunStatus.office"), //
    ERROR(4, "profileRatingRunStatus.office"); //

    private final Integer value;
    private final String code;

    private ProfileRatingRunStatus(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static ProfileRatingRunStatus fromInt(final Integer frequency) {
        ProfileRatingRunStatus typeEnums = ProfileRatingRunStatus.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case 1:
                    typeEnums = ProfileRatingRunStatus.INITIATED;
                break;
                case 2:
                    typeEnums = ProfileRatingRunStatus.INPROGRESS;
                break;
                case 3:
                    typeEnums = ProfileRatingRunStatus.COMPLETED;
                break;
                case 4:
                    typeEnums = ProfileRatingRunStatus.ERROR;
                break;
            }
        }
        return typeEnums;
    }
}
