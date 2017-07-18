package com.finflux.kyc.domain;

public enum KycType {

    INVALID(0, "kycType.invalid"), //
    AUTO(1, "kycType.auto"), //
    MANUAL(2, "kycType.manual");

    private final Integer value;
    private final String code;

    private KycType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public KycType fromInt(final Integer type) {

        KycType kycType = KycType.INVALID;
        switch (type) {
            case 1:
                kycType = KycType.AUTO;
            break;
            case 2:
                kycType = KycType.MANUAL;
            break;

        }
        return kycType;
    }

}
