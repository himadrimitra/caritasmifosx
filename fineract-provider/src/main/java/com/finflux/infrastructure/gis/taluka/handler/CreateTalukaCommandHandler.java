package com.finflux.infrastructure.gis.taluka.handler;

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

import com.finflux.infrastructure.gis.taluka.services.TalukaWritePlatformService;

@Service
@CommandType(entity = "TALUKA", action = "CREATE")
public class CreateTalukaCommandHandler implements NewCommandSourceHandler {

    private final TalukaWritePlatformService talukaWritePlatformService;

    @Autowired
    public CreateTalukaCommandHandler(final TalukaWritePlatformService talukaWritePlatformService) {
        this.talukaWritePlatformService = talukaWritePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
        return this.talukaWritePlatformService.create(command.entityId(), command);

    }

}
