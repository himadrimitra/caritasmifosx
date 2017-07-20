package com.finflux.portfolio.bank.handler;

import com.finflux.portfolio.bank.service.BankAccountDetailsWriteService;
import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "BANKACCOUNTDETAIL", action = "ACTIVATE")
public class ActivateBankAccountDetailCommandHandler implements NewCommandSourceHandler {

    private final BankAccountDetailsWriteService writePlatformService;

    @Autowired
    public ActivateBankAccountDetailCommandHandler(final BankAccountDetailsWriteService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.activate(command);
    }
}