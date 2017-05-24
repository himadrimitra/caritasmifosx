package com.finflux.infrastructure.cryptography.data;

import com.finflux.infrastructure.cryptography.api.CryptographyApiConstants;

public enum CryptographyEntityType {

    INVALID(0, "cryptographyEntityType.invalid"), //
    LOGIN(1, "cryptographyEntityType.login");//

    private final Integer value;
    private final String code;

    private CryptographyEntityType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static CryptographyEntityType fromString(final String entityType) {
        CryptographyEntityType cryptographyEntityType = CryptographyEntityType.INVALID;
        if (entityType != null) {
            switch (entityType) {
                case CryptographyApiConstants.entityTypeLogin:
                    cryptographyEntityType = CryptographyEntityType.LOGIN;
                break;
            }
        }
        return cryptographyEntityType;
    }

    public boolean isLogin() {
        return this.value.equals(CryptographyEntityType.LOGIN.getValue());
    }
}
