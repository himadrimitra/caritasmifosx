/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.client.service;

import java.util.Map;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.codes.exception.CodeValueNotFoundException;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.command.ClientIdentifierCommand;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientIdentifier;
import org.apache.fineract.portfolio.client.domain.ClientIdentifierRepository;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.exception.ClientIdentifierNotFoundException;
import org.apache.fineract.portfolio.client.exception.DuplicateClientIdentifierException;
import org.apache.fineract.portfolio.client.serialization.ClientIdentifierCommandFromApiJsonDeserializer;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.validations.domain.EntityValiDationType;
import org.apache.fineract.portfolio.validations.service.FieldRegexValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.finflux.common.util.FinfluxCollectionUtils;

@Service
public class ClientIdentifierWritePlatformServiceJpaRepositoryImpl implements ClientIdentifierWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(ClientIdentifierWritePlatformServiceJpaRepositoryImpl.class);

    private final PlatformSecurityContext context;
    private final ClientRepositoryWrapper clientRepository;
    private final ClientIdentifierRepository clientIdentifierRepository;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final ClientIdentifierCommandFromApiJsonDeserializer clientIdentifierCommandFromApiJsonDeserializer;
    private final FieldRegexValidator fieldRegexValidator;
    private final BusinessEventNotifierService businessEventNotifierService;

    @Autowired
    public ClientIdentifierWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
            final FieldRegexValidator fieldRegexValidator, final ClientRepositoryWrapper clientRepository,
            final ClientIdentifierRepository clientIdentifierRepository, final CodeValueRepositoryWrapper codeValueRepository,
            final ClientIdentifierCommandFromApiJsonDeserializer clientIdentifierCommandFromApiJsonDeserializer,
            final BusinessEventNotifierService businessEventNotifierService) {
        this.context = context;
        this.clientRepository = clientRepository;
        this.clientIdentifierRepository = clientIdentifierRepository;
        this.codeValueRepository = codeValueRepository;
        this.clientIdentifierCommandFromApiJsonDeserializer = clientIdentifierCommandFromApiJsonDeserializer;
        this.fieldRegexValidator = fieldRegexValidator;
        this.businessEventNotifierService = businessEventNotifierService;
    }

    @Transactional
    @Override
    public CommandProcessingResult addClientIdentifier(final Long clientId, final JsonCommand command) {

        this.context.authenticatedUser();
        final ClientIdentifierCommand clientIdentifierCommand = this.clientIdentifierCommandFromApiJsonDeserializer
                .commandFromApiJson(command.json());
        clientIdentifierCommand.validateForCreate();

        final String documentKey = clientIdentifierCommand.getDocumentKey();
        String documentTypeLabel = null;
        Long documentTypeId = null;
        try {
            final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);

            this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.CLIENT_IDENTIFIER_ADD,
                    FinfluxCollectionUtils.constructEntityMap(BUSINESS_ENTITY.ENTITY_LOCK_STATUS, client.isLocked()));
            
            CodeValue documentType = null;
            if(null == clientIdentifierCommand.getSystemIdentifier()){
                documentType = this.codeValueRepository.findOneWithNotFoundDetection(clientIdentifierCommand
                        .getDocumentTypeId());
            }else{
                documentType = this.codeValueRepository.findOneBySystemIdentifier(clientIdentifierCommand
                        .getSystemIdentifier());
            }
            documentTypeId = documentType.getId();
            documentTypeLabel = documentType.label();
            fieldRegexValidator.validateForFieldName(EntityValiDationType.CLIENTIDENTIFIER.getValue(), command);
            final ClientIdentifier clientIdentifier = ClientIdentifier.fromJson(client, documentType, command);

            this.clientIdentifierRepository.save(clientIdentifier);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withOfficeId(client.officeId()) //
                    .withClientId(clientId) //
                    .withEntityId(clientIdentifier.getId()) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleClientIdentifierDataIntegrityViolation(documentTypeLabel, documentTypeId, documentKey, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult updateClientIdentifier(final Long clientId, final Long identifierId, final JsonCommand command) {

        this.context.authenticatedUser();
        final ClientIdentifierCommand clientIdentifierCommand = this.clientIdentifierCommandFromApiJsonDeserializer
                .commandFromApiJson(command.json());
        clientIdentifierCommand.validateForUpdate();

        String documentTypeLabel = null;
        String documentKey = null;
        Long documentTypeId = clientIdentifierCommand.getDocumentTypeId();
        try {
            CodeValue documentType = null;

            final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);
            final ClientIdentifier clientIdentifierForUpdate = this.clientIdentifierRepository.findOne(identifierId);
            if (clientIdentifierForUpdate == null) { throw new ClientIdentifierNotFoundException(identifierId); }
            this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.CLIENT_IDENTIFIER_UPDATE,
                    FinfluxCollectionUtils.constructEntityMap(BUSINESS_ENTITY.ENTITY_LOCK_STATUS, clientIdentifierForUpdate.isLocked()));
            final Map<String, Object> changes = clientIdentifierForUpdate.update(command);
            if (changes.containsKey("documentTypeId")) {
                documentType = this.codeValueRepository.findOneWithNotFoundDetection(documentTypeId);
                if (documentType == null) { throw new CodeValueNotFoundException(documentTypeId); }

                documentTypeId = documentType.getId();
                documentTypeLabel = documentType.label();
                clientIdentifierForUpdate.update(documentType);
            }

            if (changes.containsKey("documentTypeId") && changes.containsKey("documentKey")) {
                documentTypeId = clientIdentifierCommand.getDocumentTypeId();
                documentKey = clientIdentifierCommand.getDocumentKey();
            } else if (changes.containsKey("documentTypeId") && !changes.containsKey("documentKey")) {
                documentTypeId = clientIdentifierCommand.getDocumentTypeId();
                documentKey = clientIdentifierForUpdate.documentKey();
            } else if (!changes.containsKey("documentTypeId") && changes.containsKey("documentKey")) {
                documentTypeId = clientIdentifierForUpdate.documentTypeId();
                documentKey = clientIdentifierForUpdate.documentKey();
            }

            if (!changes.isEmpty()) {
                this.clientIdentifierRepository.saveAndFlush(clientIdentifierForUpdate);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withOfficeId(client.officeId()) //
                    .withClientId(clientId) //
                    .withEntityId(identifierId) //
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleClientIdentifierDataIntegrityViolation(documentTypeLabel, documentTypeId, documentKey, dve);
            return new CommandProcessingResult(Long.valueOf(-1));
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteClientIdentifier(final Long clientId, final Long identifierId, final Long commandId) {

        final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);

        final ClientIdentifier clientIdentifier = this.clientIdentifierRepository.findOne(identifierId);
        if (clientIdentifier == null) { throw new ClientIdentifierNotFoundException(identifierId); }
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.CLIENT_IDENTIFIER_DELETE,
                FinfluxCollectionUtils.constructEntityMap(BUSINESS_ENTITY.ENTITY_LOCK_STATUS, clientIdentifier.isLocked()));
        this.clientIdentifierRepository.delete(clientIdentifier);

        return new CommandProcessingResultBuilder() //
                .withCommandId(commandId) //
                .withOfficeId(client.officeId()) //
                .withClientId(clientId) //
                .withEntityId(identifierId) //
                .build();
    }

    private void handleClientIdentifierDataIntegrityViolation(final String documentTypeLabel, final Long documentTypeId,
            final String documentKey, final DataIntegrityViolationException dve) {

        if (dve.getMostSpecificCause().getMessage().contains("unique_client_identifier")) {
            
        } else if (dve.getMostSpecificCause().getMessage().contains("unique_active_client_identifier")) {
            throw new DuplicateClientIdentifierException(documentTypeLabel);
        } else if (dve.getMostSpecificCause().getMessage().contains("unique_identifier_key")) { throw new DuplicateClientIdentifierException(
                documentTypeId, documentTypeLabel, documentKey); }

        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("error.msg.clientIdentifier.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }
}