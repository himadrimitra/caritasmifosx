package com.finflux.transaction.execution.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class BankAccountTransactionNotFoundException extends AbstractPlatformResourceNotFoundException {

    public BankAccountTransactionNotFoundException(final Long id) {
        super("error.msg.bank.account.transaction.not.found", "BankAccountTransaction with " + id + " does not exist", id);
    }
}
