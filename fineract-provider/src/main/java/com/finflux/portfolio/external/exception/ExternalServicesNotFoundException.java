package com.finflux.portfolio.external.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ExternalServicesNotFoundException extends AbstractPlatformResourceNotFoundException {

    public ExternalServicesNotFoundException(final Long id) {
        super("error.msg.external.service.detail.not.found", "External Service with " + id + " does not exist", id);
    }
}
