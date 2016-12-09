package com.finflux.portfolio.bank.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class BankAccountDetailAssociationExistsException extends AbstractPlatformResourceNotFoundException {

    public BankAccountDetailAssociationExistsException(final Long entityId, final Integer entityTypeId) {
        super("error.msg.bank.account.detail.already.exist", "BankAccountDetail for entity with " + entityId + " already exist", entityId,
                entityTypeId);
    }
}
