package com.finflux.portfolio.loan.utilization.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.loan.utilization.service.LoanUtilizationCheckWritePlatformService;

@Service
@CommandType(entity = "LOANUTILIZATIONCHECK", action = "UPDATE")
public class UpdateLoanUtilizationCheckCommandHandler implements NewCommandSourceHandler {

    private final LoanUtilizationCheckWritePlatformService writePlatformService;

    @Autowired
    public UpdateLoanUtilizationCheckCommandHandler(final LoanUtilizationCheckWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.update(command.entityId(), command.subentityId(), command);
    }
}
