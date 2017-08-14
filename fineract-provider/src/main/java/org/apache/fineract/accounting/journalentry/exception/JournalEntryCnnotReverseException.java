/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package org.apache.fineract.accounting.journalentry.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * A {@link RuntimeException} thrown when a GL Journal Entry is Invalid
 */
public class JournalEntryCnnotReverseException extends AbstractPlatformDomainRuleException {

    public JournalEntryCnnotReverseException(final Long transactionId) {
        super("error.msg.journalEntries.cannot.reverse",
                "Journal Entries with transaction Identifier " + transactionId + " cannot reverse since it is a reversal journal entry",
                transactionId);
    }

}