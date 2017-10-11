package com.finflux.risk.creditbureau.configuration.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class CreditBureauProductNotFoundException extends AbstractPlatformResourceNotFoundException {

    public CreditBureauProductNotFoundException(final Long id) {
        super("error.msg.credit.bureau.product.id.invalid", "Credit bureau product id " + id + " does not exist",
                id);
    }
}