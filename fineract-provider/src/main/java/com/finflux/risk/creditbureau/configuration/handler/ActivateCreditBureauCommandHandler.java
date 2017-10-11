package com.finflux.risk.creditbureau.configuration.handler;

import com.finflux.risk.creditbureau.configuration.service.CreditBureauProductWritePlatformService;
import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "CREDITBUREAU", action = "ACTIVATE")
public class ActivateCreditBureauCommandHandler implements NewCommandSourceHandler {

    private final CreditBureauProductWritePlatformService writePlatformService;

    @Autowired
    public ActivateCreditBureauCommandHandler(final CreditBureauProductWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {

        return this.writePlatformService.activateCreditBureau(command.entityId());
    }
}
