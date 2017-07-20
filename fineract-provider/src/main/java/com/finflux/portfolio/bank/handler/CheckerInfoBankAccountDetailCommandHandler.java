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
@CommandType(entity = "BANKACCOUNTDETAIL", action = "CHECKERINFO")
public class CheckerInfoBankAccountDetailCommandHandler implements NewCommandSourceHandler {

    private final BankAccountDetailsWriteService writePlatformService;

    @Autowired
    public CheckerInfoBankAccountDetailCommandHandler(final BankAccountDetailsWriteService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.updateCheckerInfo(command);
    }

}
