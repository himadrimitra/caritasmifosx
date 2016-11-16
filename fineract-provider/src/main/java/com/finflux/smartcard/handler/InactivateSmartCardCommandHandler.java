package com.finflux.smartcard.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.smartcard.services.SmartCardWritePlatformServices;

@Service
@CommandType(entity = "SMARTCARD", action = "INACTIVATE")
public class InactivateSmartCardCommandHandler implements NewCommandSourceHandler {

	private final SmartCardWritePlatformServices smartCardWritePlatformServices;

	@Autowired
	public InactivateSmartCardCommandHandler(final SmartCardWritePlatformServices smartCardWritePlatformServices) {
		this.smartCardWritePlatformServices = smartCardWritePlatformServices;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.smartCardWritePlatformServices.inActivate(command.getClientId(), command.subentityId().intValue(),
				command);
	}

}
