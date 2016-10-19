/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;


public class BankStatementNotFoundException extends AbstractPlatformResourceNotFoundException {

    public BankStatementNotFoundException(final Long id) {
        super("error.msg.bank.statement.id.invalid", "Bank Statement with identifier " + id + " does not exist", id);
    }

}
