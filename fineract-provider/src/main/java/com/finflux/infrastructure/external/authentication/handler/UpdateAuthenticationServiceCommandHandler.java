package com.finflux.infrastructure.external.authentication.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.external.authentication.service.ExternalAuthenticationServicesWritePlatformService;

@Service
@CommandType(entity = "AUTHENTICATIONSERVICE", action = "UPDATE")
public class UpdateAuthenticationServiceCommandHandler implements NewCommandSourceHandler {
	private final ExternalAuthenticationServicesWritePlatformService authenticationServicesWritePlatformService;

	@Autowired
	public UpdateAuthenticationServiceCommandHandler(
			final ExternalAuthenticationServicesWritePlatformService authenticationServicesWritePlatformService) {
		this.authenticationServicesWritePlatformService = authenticationServicesWritePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.authenticationServicesWritePlatformService
				.updateTransactionAuthenticationService(command.entityId(), command);
	}
}
