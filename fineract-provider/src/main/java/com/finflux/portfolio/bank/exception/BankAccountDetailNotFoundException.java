package com.finflux.portfolio.bank.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class BankAccountDetailNotFoundException extends AbstractPlatformResourceNotFoundException {

    public BankAccountDetailNotFoundException(final Long id) {
        super("error.msg.bank.account.detail.not.found", "BankAccountDetail with " + id + " does not exist", id);
    }

    public BankAccountDetailNotFoundException(final Long entityId, final Integer entityTypeId) {
        super("error.msg.bank.account.detail.not.found", "BankAccountDetail for entity with " + entityId + " does not exist", entityId,
                entityTypeId);
    }
}
