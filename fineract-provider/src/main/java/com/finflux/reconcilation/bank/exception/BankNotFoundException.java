package com.finflux.reconcilation.bank.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;


public class BankNotFoundException extends AbstractPlatformResourceNotFoundException {

    public BankNotFoundException(final Long id) {
        super("error.msg.bank.id.invalid", "Bank with identifier " + id + " does not exist", id);
    }

}
