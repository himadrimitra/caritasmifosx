/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class GroupLoanIndividualMonitoringTransactionNotFoundException extends AbstractPlatformResourceNotFoundException {

    public GroupLoanIndividualMonitoringTransactionNotFoundException(Long id) {
        super("error.msg.glim.transaction.id.invalid",
                "Group Loan individual transaction record with identifier " + id + " does not exist", id);
    }

}
