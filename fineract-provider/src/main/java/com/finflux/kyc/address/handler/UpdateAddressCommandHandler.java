package com.finflux.kyc.address.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.kyc.address.service.AddressWritePlatformService;

@Service
@CommandType(entity = "ADDRESSES", action = "UPDATE")
public class UpdateAddressCommandHandler implements NewCommandSourceHandler {

    private final AddressWritePlatformService writePlatformService;

    @Autowired
    public UpdateAddressCommandHandler(final AddressWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.update(command.entityId(),command.subentityId(), command);
    }
}
