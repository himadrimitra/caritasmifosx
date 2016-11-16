package com.finflux.smartcard.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class SmartCardEntityTypeNotSupportedException  extends AbstractPlatformDomainRuleException {

	public SmartCardEntityTypeNotSupportedException(final String resource) {
        super("smartcard.entitytype.not.supported", "SmartCard is not supported for the resource " + resource, resource);
    }
}
