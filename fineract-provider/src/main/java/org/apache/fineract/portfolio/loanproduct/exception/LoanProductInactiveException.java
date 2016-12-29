package org.apache.fineract.portfolio.loanproduct.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class LoanProductInactiveException extends AbstractPlatformResourceNotFoundException{
	
	public LoanProductInactiveException(final Long id) {
		super("error.msg.loanproduct.id.inactive", "Loan product with identifier " + id + " is closed.", id);
	}

}
