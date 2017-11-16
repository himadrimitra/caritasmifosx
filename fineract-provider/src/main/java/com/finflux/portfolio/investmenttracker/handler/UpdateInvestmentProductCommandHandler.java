package com.finflux.portfolio.investmenttracker.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.investmenttracker.service.InvestmentProductWriteService;

@Service
@CommandType(entity = "INVESTMENT_PRODUCT", action = "UPDATE")
public class UpdateInvestmentProductCommandHandler implements NewCommandSourceHandler {

    private final InvestmentProductWriteService investmentProductWriteService;

    @Autowired
    public UpdateInvestmentProductCommandHandler(final InvestmentProductWriteService investmentProductWriteService) {
        this.investmentProductWriteService = investmentProductWriteService;
    }

    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.investmentProductWriteService.updateInvestmentProduct(command.entityId(), command);
    }

}
