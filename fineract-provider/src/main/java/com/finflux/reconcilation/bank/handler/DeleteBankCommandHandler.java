/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bank.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.reconcilation.ReconciliationApiConstants;
import com.finflux.reconcilation.bank.service.BankWritePlatformService;

@Service
@CommandType(entity = ReconciliationApiConstants.BANK_RESOURCE_NAME, action = ReconciliationApiConstants.DELETE_ACTION)
public class DeleteBankCommandHandler implements NewCommandSourceHandler {

    private final BankWritePlatformService bankWritePlatformService;

    @Autowired
    public DeleteBankCommandHandler(final BankWritePlatformService bankWritePlatformService) {
        this.bankWritePlatformService = bankWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {

        return this.bankWritePlatformService.deleteBank(command.entityId());
    }
}
