package com.finflux.reconcilation.bankstatement.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;


public class BankStatementNotFoundException extends AbstractPlatformResourceNotFoundException {

    public BankStatementNotFoundException(final Long id) {
        super("error.msg.bank.statement.id.invalid", "Bank Statement with identifier " + id + " does not exist", id);
    }

}
