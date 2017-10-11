package com.finflux.reconcilation.bankstatement.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.reconcilation.ReconciliationApiConstants;
import com.finflux.reconcilation.bankstatement.service.BankStatementDetailsWritePlatformService;

@Service
@CommandType(entity = ReconciliationApiConstants.BANK_STATEMENT_DETAILS_RESOURCE_NAME, action = ReconciliationApiConstants.UPDATE_ACTION)
public class UpdateBankStatementDetailsCommandHandler implements NewCommandSourceHandler {

    private final BankStatementDetailsWritePlatformService bankStatementDetailsWritePlatformService;

    @Autowired
    public UpdateBankStatementDetailsCommandHandler(
            final BankStatementDetailsWritePlatformService bankStatementDetailsWritePlatformService) {
        this.bankStatementDetailsWritePlatformService = bankStatementDetailsWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {

        return this.bankStatementDetailsWritePlatformService.updateBankStatementDetails(command.entityId(), command);
    }

}
