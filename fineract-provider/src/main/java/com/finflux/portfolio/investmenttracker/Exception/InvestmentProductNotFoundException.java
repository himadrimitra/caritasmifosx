package com.finflux.portfolio.investmenttracker.Exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class InvestmentProductNotFoundException extends AbstractPlatformResourceNotFoundException {

    public InvestmentProductNotFoundException(final Long id) {
        super("error.msg.investmentproduct.id.invalid", "Investment product with identifier " + id + " does not exist", id);
    }
}
