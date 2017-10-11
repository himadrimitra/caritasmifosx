package com.finflux.fingerprint.services;

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
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.finflux.fingerprint.api.FingerPrintApiConstants;
import com.finflux.fingerprint.data.FingerPrintDataValidator;
import com.finflux.fingerprint.data.FingerPrintEntityTypeEnums;
import com.finflux.fingerprint.domain.FingerPrint;
import com.finflux.fingerprint.domain.FingerPrintRepositoryWrapper;

@Service
public class FingerPrintWritePlatformServicesImpl implements FingerPrintWritePlatformServices {

    private final static Logger logger = LoggerFactory.getLogger(FingerPrintWritePlatformServicesImpl.class);

    private final PlatformSecurityContext context;
    private final FingerPrintRepositoryWrapper fingerPrintRepository;
    private final FingerPrintDataValidator fingerPrintValidator;
    private final ClientRepositoryWrapper clientRespository;

    @Autowired
    public FingerPrintWritePlatformServicesImpl(final PlatformSecurityContext context,
            final FingerPrintRepositoryWrapper fingerPrintRepository, final FingerPrintDataValidator fingerPrintValidator,
            final ClientRepositoryWrapper clientRespository) {
        this.context = context;
        this.fingerPrintRepository = fingerPrintRepository;
        this.fingerPrintValidator = fingerPrintValidator;
        this.clientRespository = clientRespository;
    }

   
    @Transactional
    @Override
    public CommandProcessingResult create(final Long clientId, final JsonCommand command) {

        try {

            this.clientRespository.findOneWithNotFoundDetection(clientId);
            this.context.authenticatedUser();
            this.fingerPrintValidator.validateForCreate(clientId, command.json());

            FingerPrint fingerPrint = FingerPrint.create(clientId, command);

            this.fingerPrintRepository.save(fingerPrint);
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(fingerPrint.getId()) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(clientId, command, dve);
            return CommandProcessingResult.empty();
        }
    }

    private void handleDataIntegrityIssues(final Long clientId, final JsonCommand command, DataIntegrityViolationException dve) {

        /**
         * Checking for duplicate finger print
         */

        final Throwable realCause = dve.getMostSpecificCause();

        if (realCause.getMessage().contains("f_client_fingerprint_UNIQUE")) {
            final Integer fingerId = command.integerValueOfParameterNamed(FingerPrintApiConstants.fingerIdParamName);
            final FingerPrintEntityTypeEnums fingerType = FingerPrintEntityTypeEnums.fromInt(fingerId);
            throw new PlatformDataIntegrityException("error.msg.finger.duplicate",
                    "FingerId `" + fingerType.getCode() + "` already exists for client", "ClientId `" + clientId + "`", "entityId", "fingerId",
                    clientId, fingerType.getCode());
        }

        logAsErrorUnexpectedDataIntegrityException(dve);

        throw new PlatformDataIntegrityException("error.msg.fingerId.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }
}
