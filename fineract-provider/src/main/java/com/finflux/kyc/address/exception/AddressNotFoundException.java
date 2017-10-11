package com.finflux.kyc.address.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class AddressNotFoundException extends AbstractPlatformResourceNotFoundException {

    public AddressNotFoundException(final Long id) {
        super("error.msg.address.id.invalid", "Address with identifier " + id + " does not exist", id);
    }

}
