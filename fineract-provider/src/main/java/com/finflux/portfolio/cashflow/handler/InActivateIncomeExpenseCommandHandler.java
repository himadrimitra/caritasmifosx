package com.finflux.portfolio.cashflow.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.cashflow.service.IncomeExpenseWritePlatformService;

@Service
@CommandType(entity = "INCOMEEXPENSE", action = "INACTIVATE")
public class InActivateIncomeExpenseCommandHandler implements NewCommandSourceHandler {

    private final IncomeExpenseWritePlatformService writePlatformService;

    @Autowired
    public InActivateIncomeExpenseCommandHandler(final IncomeExpenseWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.inActivate(command.entityId(), command);
    }
}