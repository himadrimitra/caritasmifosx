package com.finflux.fingerprint.handler;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.fingerprint.services.FingerPrintWritePlatformServices;

@Service
@CommandType(entity = "FINGERPRINT", action = "CREATE")
public class CreateFingerPrintCommandHandler implements NewCommandSourceHandler {

    private final FingerPrintWritePlatformServices fingerPrintWritePlatformServices;

    @Autowired
    public CreateFingerPrintCommandHandler(final FingerPrintWritePlatformServices fingerPrintWritePlatformServices) {
        this.fingerPrintWritePlatformServices = fingerPrintWritePlatformServices;
    }

    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {

        return this.fingerPrintWritePlatformServices.create(command.getClientId(), command);
    }

}
