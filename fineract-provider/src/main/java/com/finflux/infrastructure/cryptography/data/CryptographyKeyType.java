package com.finflux.infrastructure.cryptography.data;

public enum CryptographyKeyType {

    INVALID(0, "cryptographyKeyType.invalid"), //
    PUBLIC(1, "cryptographyKeyType.public"), //
    PRIVATE(2, "cryptographyKeyType.private"); //

    private final Integer value;
    private final String code;

    private CryptographyKeyType(final Integer value, final String code) {
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