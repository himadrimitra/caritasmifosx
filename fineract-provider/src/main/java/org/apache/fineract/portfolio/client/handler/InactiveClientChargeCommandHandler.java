package org.apache.fineract.portfolio.client.handler;

import javax.transaction.Transactional;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.service.ClientChargeWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = ClientApiConstants.CLIENT_RECURRING_CHARGES_RESOURCE_NAME, action = ClientApiConstants.CLIENT_RECURRING_CHARGE_ACTION_INACTIVATE)
public class InactiveClientChargeCommandHandler implements NewCommandSourceHandler {

    private final ClientChargeWritePlatformService writePlatformService;

    @Autowired
    public InactiveClientChargeCommandHandler(ClientChargeWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
        // TODO Auto-generated method stub
        return this.writePlatformService.inactivateCharge(command.getClientId(), command.entityId());
    }

}
