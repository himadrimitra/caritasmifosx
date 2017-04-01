package com.finflux.infrastructure.cryptography.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class CryptographyKeyNotFoundException extends AbstractPlatformResourceNotFoundException {

    public CryptographyKeyNotFoundException(final String keyType) {
        super("error.msg.cryptography.key.not.exist", ""+keyType+" does not exist", keyType);
    }
}
