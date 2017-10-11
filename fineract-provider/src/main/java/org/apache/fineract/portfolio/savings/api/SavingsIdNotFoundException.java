package org.apache.fineract.portfolio.savings.api;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class SavingsIdNotFoundException extends AbstractPlatformResourceNotFoundException {

	 public SavingsIdNotFoundException(final Long savingsId) {
	        super("error.msg.default.savings.id.for.client.not.found", "SavingsId " + savingsId
	        		+ " does not exist", savingsId);
	    }
}
