package com.finflux.reconcilation.bank.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.reconcilation.ReconciliationApiConstants;
import com.finflux.reconcilation.bank.service.BankWritePlatformService;

@Service
@CommandType(entity = ReconciliationApiConstants.BANK_RESOURCE_NAME, action = ReconciliationApiConstants.UPDATE_ACTION)
public class UpdateBankCommandHandler implements NewCommandSourceHandler {

    private final BankWritePlatformService bankWritePlatformService;

    @Autowired
    public UpdateBankCommandHandler(final BankWritePlatformService bankWritePlatformService) {
        this.bankWritePlatformService = bankWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {

        return this.bankWritePlatformService.updateBank(command.entityId(), command);
    }
}
