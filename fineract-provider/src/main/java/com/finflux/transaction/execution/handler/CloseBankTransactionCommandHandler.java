/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.transaction.execution.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.transaction.execution.service.BankTransactionWriteService;

@Service
@CommandType(entity = "BANK_TRANSACTION", action = "CLOSE")
public class CloseBankTransactionCommandHandler implements NewCommandSourceHandler {

    private final BankTransactionWriteService bankTransactionWriteService;

    @Autowired
    public CloseBankTransactionCommandHandler(final BankTransactionWriteService bankTransactionWriteService) {
        this.bankTransactionWriteService = bankTransactionWriteService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.bankTransactionWriteService.closeTransaction(command.entityId());
    }
}