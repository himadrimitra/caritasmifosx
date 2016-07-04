package com.finflux.reconcilation.bankstatement.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;


public class BankStatementDetailNotFoundException extends AbstractPlatformResourceNotFoundException {

    public BankStatementDetailNotFoundException(final Long id) {
        super("error.msg.bank.statement.detail.id.invalid", "Bank Statement detail with identifier " + id + " does not exist", id);
    }

}
