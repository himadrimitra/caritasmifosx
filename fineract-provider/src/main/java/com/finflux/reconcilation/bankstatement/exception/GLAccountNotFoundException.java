package com.finflux.reconcilation.bankstatement.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

@SuppressWarnings("serial")
public class GLAccountNotFoundException extends AbstractPlatformResourceNotFoundException {

    public GLAccountNotFoundException(String glCode) {
        super("GL Account not found by gl code " + glCode, "error.msg.glaccount.not.found.by.glcode");
    }
}