package com.finflux.organisation.transaction.authentication.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class TransactionAuthenticationRuleAlreadyExistException extends AbstractPlatformDomainRuleException {

	public TransactionAuthenticationRuleAlreadyExistException(String defaultUserMessageArgs) {
		super("Transaction.Rule.already.exist",
				"The transaction authentication rule already exist " + defaultUserMessageArgs, defaultUserMessageArgs);
		// TODO Auto-generated constructor stub
	}

}
