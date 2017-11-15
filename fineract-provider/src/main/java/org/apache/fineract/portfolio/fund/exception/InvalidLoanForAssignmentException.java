/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.fund.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvalidLoanForAssignmentException extends AbstractPlatformDomainRuleException {

    public InvalidLoanForAssignmentException(String loanIds) {
        super("error.msg.fund.mapping.loan.invalid.loan.id", "Invalid loan id for fund mapping " + loanIds, loanIds);
    }

}