package com.finflux.vouchers.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class VoucherCannotBeReversedException extends AbstractPlatformDomainRuleException {

    public VoucherCannotBeReversedException(final String errorCode, final Long voucherId) {
        super(errorCode, "Voucher with identifier " + voucherId + " can not be reversed", voucherId);
    }
}
