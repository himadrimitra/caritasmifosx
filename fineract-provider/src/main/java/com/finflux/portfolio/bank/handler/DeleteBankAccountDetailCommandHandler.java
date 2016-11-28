package com.finflux.portfolio.bank.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.portfolio.bank.service.BankAccountDetailsWriteService;

@Service
@CommandType(entity = "BANKACCOUNTDETAIL", action = "DELETE")
public class DeleteBankAccountDetailCommandHandler implements NewCommandSourceHandler {

    private final BankAccountDetailsWriteService writePlatformService;

    @Autowired
    public DeleteBankAccountDetailCommandHandler(final BankAccountDetailsWriteService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.delete(command);
    }
}