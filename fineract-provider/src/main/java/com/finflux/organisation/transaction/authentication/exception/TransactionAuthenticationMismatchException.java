package com.finflux.organisation.transaction.authentication.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class TransactionAuthenticationMismatchException extends AbstractPlatformDomainRuleException {

	public TransactionAuthenticationMismatchException(final Long transactionAuthenticationId) {
		super("authentication.rule.id.mismatch",
				"authentication rule id provided does not match the transaction authentication rule.",
				transactionAuthenticationId);
	}

}
