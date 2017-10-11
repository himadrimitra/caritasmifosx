package com.finflux.risk.existingloans.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ExistingLoanNotFoundException extends AbstractPlatformResourceNotFoundException {
	         public ExistingLoanNotFoundException(final Long id) {
	        super("error.msg.existingloan.id.invalid", "ExistingLoan with identifier " + id + " does not exist", id);
	    }
	}


