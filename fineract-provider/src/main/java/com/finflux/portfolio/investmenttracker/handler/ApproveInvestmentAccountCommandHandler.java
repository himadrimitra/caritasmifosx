package com.finflux.portfolio.investmenttracker.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.investmenttracker.service.InvestmentAccountWritePlatformService;

@Service
@CommandType(entity = "INVESTMENT_ACCOUNT", action = "APPROVE")
public class ApproveInvestmentAccountCommandHandler implements NewCommandSourceHandler {

    private InvestmentAccountWritePlatformService investmentAccountWritePlatformService;

    @Autowired
    public ApproveInvestmentAccountCommandHandler(final InvestmentAccountWritePlatformService investmentAccountWritePlatformService) {
        this.investmentAccountWritePlatformService = investmentAccountWritePlatformService;
    }
    
    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
        return this.investmentAccountWritePlatformService.approveInvestmentAccount(command.entityId(), command);
    }

}