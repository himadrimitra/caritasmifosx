package com.finflux.kyc.address.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class AddressTypeAlreadyExistsException extends AbstractPlatformDomainRuleException {

    public AddressTypeAlreadyExistsException(final String addressType, final String resource, Long entityId) {
        super("error.msg." + addressType + ".address.with.addresstype.already.exists", "Address with address type `" + addressType
                + "` already exists for resource" + resource + " with identifier `" + entityId + "`", addressType, resource, entityId);
    }

}
