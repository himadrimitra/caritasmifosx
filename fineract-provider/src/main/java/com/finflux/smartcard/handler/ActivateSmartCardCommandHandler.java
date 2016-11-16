package com.finflux.smartcard.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.smartcard.services.SmartCardWritePlatformServices;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

@Service
@CommandType(entity = "SMARTCARD", action = "ACTIVATE")
public class ActivateSmartCardCommandHandler implements NewCommandSourceHandler {

	private final SmartCardWritePlatformServices smartCardWritePlatformServices;

	@Autowired
	public ActivateSmartCardCommandHandler(final SmartCardWritePlatformServices smartCardWritePlatformServices) {
		this.smartCardWritePlatformServices = smartCardWritePlatformServices;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {

		return this.smartCardWritePlatformServices.activate(command.getClientId(), command.subentityId().intValue(),
				command);
	}

}
