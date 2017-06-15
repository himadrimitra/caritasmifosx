package org.apache.fineract.portfolio.client.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.client.service.ClientAccountLimitsWritePlatformServeice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "CUSTOMERACCOUNTLIMITS", action = "CREATE")
public class CreateAccountLimitsCommandHandler implements NewCommandSourceHandler {

    private final ClientAccountLimitsWritePlatformServeice accountLimitsWritePlatformService;

    @Autowired
    public CreateAccountLimitsCommandHandler(final ClientAccountLimitsWritePlatformServeice accountLimitsWritePlatformService) {
        this.accountLimitsWritePlatformService = accountLimitsWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.accountLimitsWritePlatformService.createAccountLimits(command.getClientId(), command);
    }
}