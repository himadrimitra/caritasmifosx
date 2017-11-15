package com.finflux.portfolio.investmenttracker.Exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;


public class InvestmentAccountNotFoundException extends AbstractPlatformResourceNotFoundException{

    public InvestmentAccountNotFoundException(final Long id) {
        super("error.msg.investmentaccount.id.invalid", "Investment Account with identifier " + id + " does not exist", id);
    }
}
