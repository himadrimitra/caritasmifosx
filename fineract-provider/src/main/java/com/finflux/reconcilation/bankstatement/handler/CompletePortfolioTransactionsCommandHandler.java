package com.finflux.reconcilation.bankstatement.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.reconcilation.ReconciliationApiConstants;
import com.finflux.reconcilation.bankstatement.service.BankStatementWritePlatformService;

@Service
@CommandType(entity = ReconciliationApiConstants.BULK_PORTFOLIO_TRANSACTIONS, action = ReconciliationApiConstants.CREATE_ACTION)
public class CompletePortfolioTransactionsCommandHandler implements NewCommandSourceHandler{
    private final BankStatementWritePlatformService bankStatementWritePlatformService;

    @Autowired
    public CompletePortfolioTransactionsCommandHandler(final BankStatementWritePlatformService bankStatementWritePlatformService) {
        this.bankStatementWritePlatformService = bankStatementWritePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {

        return this.bankStatementWritePlatformService.completePortfolioTransactions(command);
    }
}
