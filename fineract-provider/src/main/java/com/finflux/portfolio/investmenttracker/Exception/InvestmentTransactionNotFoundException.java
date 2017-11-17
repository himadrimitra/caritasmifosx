package com.finflux.portfolio.investmenttracker.Exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;


public class InvestmentTransactionNotFoundException extends AbstractPlatformResourceNotFoundException{

    public InvestmentTransactionNotFoundException(final Long id) {
        super("error.msg.investment.transaction.id.invalid","Transaction with idetifier "+id+" does not exist.");
    }

}
