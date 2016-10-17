package com.finflux.portfolio.loan.purpose.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.loan.purpose.service.LoanPurposeGroupWritePlatformService;

@Service
@CommandType(entity = "LOANPURPOSEGROUP", action = "ACTIVATE")
public class ActivateLoanPurposeGroupCommandHandler implements NewCommandSourceHandler {

    private final LoanPurposeGroupWritePlatformService writePlatformService;

    @Autowired
    public ActivateLoanPurposeGroupCommandHandler(final LoanPurposeGroupWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.activateLoanPurposeGroup(command.entityId(), command);
    }
}