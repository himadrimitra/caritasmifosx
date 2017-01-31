package com.finflux.portfolio.external.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ExternalServicePropertyNotFoundException extends AbstractPlatformResourceNotFoundException {

    public ExternalServicePropertyNotFoundException(final Long serviceId, final String name) {
        super("error.msg.external.service.proeprty.not.found", "External Service with " + name + " does not exist for External Service:"+serviceId, name);
    }
}
