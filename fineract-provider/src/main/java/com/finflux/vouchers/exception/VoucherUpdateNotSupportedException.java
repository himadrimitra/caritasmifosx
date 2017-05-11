package com.finflux.vouchers.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class VoucherUpdateNotSupportedException extends AbstractPlatformDomainRuleException {

    public VoucherUpdateNotSupportedException(String voucherType) {
        super("error.msg.vouchers.voucher.update.not.supported", "Updading voucher is not supported for voucherType ", voucherType);
    }

}
