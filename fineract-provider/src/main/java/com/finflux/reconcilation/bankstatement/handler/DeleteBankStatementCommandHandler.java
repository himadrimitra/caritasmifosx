package com.finflux.reconcilation.bankstatement.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.reconcilation.ReconciliationApiConstants;
import com.finflux.reconcilation.bankstatement.service.BankStatementWritePlatformService;

@Service
@CommandType(entity = ReconciliationApiConstants.BANK_STATEMENT_RESOURCE_NAME, action = "DELETE")
public class DeleteBankStatementCommandHandler implements NewCommandSourceHandler {

    private final BankStatementWritePlatformService bankStatementWritePlatformService;

    @Autowired
    public DeleteBankStatementCommandHandler(final BankStatementWritePlatformService bankStatementWritePlatformService) {
        this.bankStatementWritePlatformService = bankStatementWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {

        return this.bankStatementWritePlatformService.deleteBankStatement(command.entityId());

    }
}
