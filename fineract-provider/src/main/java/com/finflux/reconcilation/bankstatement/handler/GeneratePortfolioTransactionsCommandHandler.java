/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.reconcilation.ReconciliationApiConstants;
import com.finflux.reconcilation.bankstatement.service.BankStatementWritePlatformService;

@Service
@CommandType(entity = ReconciliationApiConstants.PORTFOLIO_TRANSACTIONS, action = ReconciliationApiConstants.CREATE_ACTION)
public class GeneratePortfolioTransactionsCommandHandler  implements NewCommandSourceHandler {

    private final BankStatementWritePlatformService bankStatementWritePlatformService;

    @Autowired
    public GeneratePortfolioTransactionsCommandHandler(final BankStatementWritePlatformService bankStatementWritePlatformService) {
        this.bankStatementWritePlatformService = bankStatementWritePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
        return this.bankStatementWritePlatformService.generatePortfolioTransactions(command);
    }
}
