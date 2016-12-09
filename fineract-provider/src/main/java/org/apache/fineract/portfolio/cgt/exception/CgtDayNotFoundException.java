package org.apache.fineract.portfolio.cgt.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class CgtDayNotFoundException  extends AbstractPlatformResourceNotFoundException {

    public CgtDayNotFoundException(final Long id) {
        super("error.msg.cgtDay.id.invalid", "CGT-DAY with identifier " + id + " does not exist", id);
    }

}
