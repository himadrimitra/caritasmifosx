/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bank.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

@SuppressWarnings("serial")
public class BankNotAssociatedExcecption extends AbstractPlatformDomainRuleException {

    public BankNotAssociatedExcecption() {
        super("error.msg.bank.not.associated.to.bankstatement.", "Bank is not associated to bankstatement.");
    }
}
