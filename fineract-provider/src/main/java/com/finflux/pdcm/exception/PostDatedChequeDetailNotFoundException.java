package com.finflux.pdcm.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class PostDatedChequeDetailNotFoundException extends AbstractPlatformResourceNotFoundException {

    public PostDatedChequeDetailNotFoundException(final Long id) {
        super("error.msg.pdc.id.invalid", "PDC details with identifier " + id + " does not exist", id);
    }

}