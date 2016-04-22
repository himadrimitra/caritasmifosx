package com.finflux.kyc.address.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class AddressEntityTypeNotSupportedException extends AbstractPlatformResourceNotFoundException {

    public AddressEntityTypeNotSupportedException(final String resource) {
        super("address.entitytype.not.supported", "Address is not supported for the resource " + resource, resource);
    }
}
