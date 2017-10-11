package com.finflux.vouchers.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when loan resources are not found.
 */
public class VoucherNotFoundException extends AbstractPlatformResourceNotFoundException {

    public VoucherNotFoundException(final Long id) {
        super("error.msg.voucher.id.invalid", "Voucher with identifier " + id + " does not exist", id);
    }
}