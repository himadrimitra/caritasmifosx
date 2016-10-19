/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.loanaccount.service.GroupLoanIndividualMonitoringTransactionWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "LOAN", action = "RECOVERYPAYMENT", option = "GLIM")
public class GLIMRecoveryRepaymentCommandHandler implements NewCommandSourceHandler {

    private final GroupLoanIndividualMonitoringTransactionWritePlatformService glimTransactionWritePlatformService;

    @Autowired
    public GLIMRecoveryRepaymentCommandHandler(
            final GroupLoanIndividualMonitoringTransactionWritePlatformService groupLoanIndividualMonitoringTransactionWritePlatformService) {
        this.glimTransactionWritePlatformService = groupLoanIndividualMonitoringTransactionWritePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
        boolean isRecoveryRepayment = true;
        return this.glimTransactionWritePlatformService.repayGLIM(command.getLoanId(), command, isRecoveryRepayment);
    }

}
