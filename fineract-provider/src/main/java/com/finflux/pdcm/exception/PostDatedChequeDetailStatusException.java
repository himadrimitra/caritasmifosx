package com.finflux.pdcm.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class PostDatedChequeDetailStatusException extends AbstractPlatformResourceNotFoundException {

    public PostDatedChequeDetailStatusException(final String globalisationMessageCode, final String defaultUserMessage, final Long id) {
        super(globalisationMessageCode, defaultUserMessage, id);
    }
}