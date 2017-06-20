/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.savings.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class SavingsAccountDebitsBlockedException extends AbstractPlatformDomainRuleException {

    public SavingsAccountDebitsBlockedException(final Long accountId) {
        super("error.msg.savings.account.debit.transaction.not.allowed",
                "Any debit transactions from " + accountId + " is not allowed, since the account is blocked for debits", accountId);
    }

}
