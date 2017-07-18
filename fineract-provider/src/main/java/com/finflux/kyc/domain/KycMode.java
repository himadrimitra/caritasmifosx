package com.finflux.kyc.domain;

public enum KycMode {

    INVALID(0, "kycMode.invalid"), //
    OTP(1, "kycMode.otp"), //
    FINGER_PRINT(2, "kycMode.finger.print"), //
    IRIS(3, "kycMode.iris");

    private final Integer value;
    private final String code;

    private KycMode(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public KycMode fromInt(final Integer type) {

        KycMode kycMode = KycMode.INVALID;
        switch (type) {
            case 1:
                kycMode = KycMode.OTP;
            break;
            case 2:
                kycMode = KycMode.FINGER_PRINT;
            break;
            case 3:
                kycMode = KycMode.IRIS;
            break;
        }
        return kycMode;
    }

}
