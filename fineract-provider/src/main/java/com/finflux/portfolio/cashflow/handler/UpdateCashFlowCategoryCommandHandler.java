package com.finflux.portfolio.cashflow.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.cashflow.service.CashFlowCategoryWritePlatformService;

@Service
@CommandType(entity = "CASHFLOW", action = "UPDATE")
public class UpdateCashFlowCategoryCommandHandler implements NewCommandSourceHandler {

    private final CashFlowCategoryWritePlatformService writePlatformService;

    @Autowired
    public UpdateCashFlowCategoryCommandHandler(final CashFlowCategoryWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.update(command.entityId(), command);
    }
}