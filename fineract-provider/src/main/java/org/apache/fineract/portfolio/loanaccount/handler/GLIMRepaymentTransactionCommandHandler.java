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
@CommandType(entity = "LOAN", action = "REPAYMENT", option = "GLIM")
public class GLIMRepaymentTransactionCommandHandler implements NewCommandSourceHandler {

    private final GroupLoanIndividualMonitoringTransactionWritePlatformService groupLoanIndividualMonitoringTransactionWritePlatformService;

    @Autowired
    public GLIMRepaymentTransactionCommandHandler(
            final GroupLoanIndividualMonitoringTransactionWritePlatformService groupLoanIndividualMonitoringTransactionWritePlatformService) {
        this.groupLoanIndividualMonitoringTransactionWritePlatformService = groupLoanIndividualMonitoringTransactionWritePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
        boolean isRecoveryRepayment = false;
        return this.groupLoanIndividualMonitoringTransactionWritePlatformService.repayGLIM(command.getLoanId(), command,
                isRecoveryRepayment);
    }

}
