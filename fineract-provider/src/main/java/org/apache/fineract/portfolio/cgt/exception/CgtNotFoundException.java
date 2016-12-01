package org.apache.fineract.portfolio.cgt.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class CgtNotFoundException extends AbstractPlatformResourceNotFoundException {

    public CgtNotFoundException(final Long id) {
        super("error.msg.cgt.id.invalid", "CGT with identifier " + id + " does not exist", id);
    }
}
