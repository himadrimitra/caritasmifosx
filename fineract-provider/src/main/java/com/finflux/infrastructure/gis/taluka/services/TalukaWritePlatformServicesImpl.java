package com.finflux.infrastructure.gis.taluka.services;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

import javax.transaction.Transactional;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.gis.taluka.api.TalukaApiConstants;
import com.finflux.infrastructure.gis.taluka.domain.Taluka;
import com.finflux.infrastructure.gis.taluka.domain.TalukaRepositoryWrapper;

@Service
public class TalukaWritePlatformServicesImpl implements TalukaWritePlatformService {
    
    private final static Logger logger = LoggerFactory.getLogger(TalukaWritePlatformServicesImpl.class);
    
    private final PlatformSecurityContext context;
    private final TalukaDataAssembler talukaDataAssembler;
    private final TalukaRepositoryWrapper repository;

    @Autowired
    public TalukaWritePlatformServicesImpl(final PlatformSecurityContext context,final TalukaDataAssembler talukaDataAssembler,final TalukaRepositoryWrapper repository) {
        this.context = context;
        this.talukaDataAssembler = talukaDataAssembler;
        this.repository = repository;
    }

    @SuppressWarnings({ "unused", "unchecked", "rawtypes" })
    @Transactional
    @Override
    public CommandProcessingResult create(final Long entityId, JsonCommand command) {
        try {
            this.context.authenticatedUser();
            
            final Taluka talukas = this.talukaDataAssembler.createTaluka(entityId,command);
            this.repository.save(talukas);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(talukas.getId())//
                    .build();
        }catch(final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
           return CommandProcessingResult.empty();
            
        }
    }
    private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

        final Throwable realCause = dve.getMostSpecificCause();

        if (realCause.getMessage().contains("UQ_sid_iso_taluka_code_name")) {
            final String talukaIsoCode = command.stringValueOfParameterNamed(TalukaApiConstants.talukaIsoCodeParamName);
            throw new PlatformDataIntegrityException("error.msg.taluka.iso.code.duplicate",
                    "Taluka iso code `" + talukaIsoCode + "` already exists", "talukaName", talukaIsoCode);
        }

        logAsErrorUnexpectedDataIntegrityException(dve);

        throw new PlatformDataIntegrityException("error.msg.taluka.name.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }
    
    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }

}
