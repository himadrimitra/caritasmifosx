package com.finflux.organisation.transaction.authentication.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class TransactionAuthenticationNotFoundException extends AbstractPlatformResourceNotFoundException {

	public TransactionAuthenticationNotFoundException(final Long id) {
		super("error.msg.Transaction.Authentication.Service.id.invalid",
				"Transaction Authentication Service with identifier " + id + " does not exist", id);
	}
	
}
