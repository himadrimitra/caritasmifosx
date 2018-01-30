package org.apache.fineract.organisation.monetary.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class CurrencyMismatchException  extends AbstractPlatformDomainRuleException {

	public CurrencyMismatchException(final String currencyCode) {
		super("error.msg.currency.currencyCode.mismatch",
				"Currency with code " + currencyCode
						+ " is mismatched.", currencyCode);
	}

}
