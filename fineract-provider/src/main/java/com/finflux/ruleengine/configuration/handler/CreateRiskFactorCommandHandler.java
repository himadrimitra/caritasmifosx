package com.finflux.ruleengine.configuration.handler;

import com.finflux.ruleengine.configuration.service.RiskConfigWritePlatformService;
import com.finflux.ruleengine.lib.data.EntityRuleType;
import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "RISKFACTOR", action = "CREATE")
public class CreateRiskFactorCommandHandler implements NewCommandSourceHandler {

    private final RiskConfigWritePlatformService writePlatformService;

    @Autowired
    public CreateRiskFactorCommandHandler(final RiskConfigWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.createRule(EntityRuleType.FACTOR,command);
    }
}