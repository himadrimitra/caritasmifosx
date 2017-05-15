package com.finflux.vouchers.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class VoucherAlreadyReversedException extends AbstractPlatformDomainRuleException {

    public VoucherAlreadyReversedException(final Long voucherId) {
        super("error.msg.vouchers.voucher.already.reversed", "Voucher with identifier " + voucherId + " is already in reversed state",
                voucherId);
    }
}
