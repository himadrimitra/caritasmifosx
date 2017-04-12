/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.rescheduleloan.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.loanaccount.rescheduleloan.service.LoanRescheduleRequestWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "RESCHEDULELOAN", action = "BULKCREATEANDAPPROVE")
public class BulkCreateAndApproveLoanRescheduleRequestCommandHandler implements NewCommandSourceHandler {

    private final LoanRescheduleRequestWritePlatformService loanRescheduleRequestWritePlatformService;

    @Autowired
    public BulkCreateAndApproveLoanRescheduleRequestCommandHandler(
            LoanRescheduleRequestWritePlatformService loanRescheduleRequestWritePlatformService) {
        this.loanRescheduleRequestWritePlatformService = loanRescheduleRequestWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(JsonCommand jsonCommand) {
        return this.loanRescheduleRequestWritePlatformService.createAndApprove(jsonCommand);
    }
}
