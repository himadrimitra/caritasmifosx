package com.finflux.vouchers.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvalidVoucherTypeException extends AbstractPlatformDomainRuleException{

	public InvalidVoucherTypeException(String voucherType) {
		super("error.msg.vouchers.voucherType.invalid", "voucherType query param is invalid", voucherType);
		// TODO Auto-generated constructor stub
	}

}
