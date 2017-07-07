package com.finflux.kyc.domain;

public enum KycSource {

    INVALID(0, "kycSource.invalid"), //
    AADHAAR(1, "kycSource.Aadhaar");

    private final Integer value;
    private final String code;

    private KycSource(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public KycSource fromInt(final Integer type) {

        KycSource kycSource = KycSource.INVALID;
        switch (type) {
            case 1:
                kycSource = KycSource.AADHAAR;
            break;

        }
        return kycSource;
    }

}
