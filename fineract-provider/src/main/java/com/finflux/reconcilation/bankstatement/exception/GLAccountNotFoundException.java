/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

@SuppressWarnings("serial")
public class GLAccountNotFoundException extends AbstractPlatformResourceNotFoundException {

    public GLAccountNotFoundException(String glCode) {
        super("GL Account not found by gl code " + glCode, "error.msg.glaccount.not.found.by.glcode");
    }
}