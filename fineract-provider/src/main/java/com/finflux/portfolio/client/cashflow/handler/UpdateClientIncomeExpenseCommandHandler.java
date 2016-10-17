package com.finflux.portfolio.client.cashflow.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.client.cashflow.service.ClientIncomeExpenseWritePlatformService;

@Service
@CommandType(entity = "CLIENTINCOMEEXPENSE", action = "UPDATE")
public class UpdateClientIncomeExpenseCommandHandler implements NewCommandSourceHandler {

    private final ClientIncomeExpenseWritePlatformService writePlatformService;

    @Autowired
    public UpdateClientIncomeExpenseCommandHandler(final ClientIncomeExpenseWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.update(command.entityId(), command);
    }
}