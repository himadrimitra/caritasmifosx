/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.infrastructure.gis.district.service;

import java.util.Map;

import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.infrastructure.gis.district.api.DistrictApiConstants;
import com.finflux.infrastructure.gis.district.domain.District;
import com.finflux.infrastructure.gis.district.domain.DistrictRepositoryWrapper;
import com.finflux.infrastructure.gis.district.serialization.DistrictCommandFromApiJsonDeserializer;
import com.finflux.infrastructure.gis.state.domain.State;
import com.finflux.infrastructure.gis.state.domain.StateRepositoryWrapper;
import com.finflux.task.data.TaskConfigEntityType;
import com.finflux.task.data.WorkflowDTO;
import com.finflux.task.domain.TaskConfigEntityTypeMapping;
import com.finflux.task.domain.TaskConfigEntityTypeMappingRepository;
import com.finflux.task.service.CreateWorkflowTaskFactory;

@Service
public class DistrictWritePlatformServiceJpaRepositoryImpl implements DistrictWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(DistrictWritePlatformServiceJpaRepositoryImpl.class);

    private final PlatformSecurityContext context;
    private final DistrictCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final DistrictRepositoryWrapper districtRepository;
    private final StateRepositoryWrapper stateRepository;
    private final ConfigurationDomainService configurationDomainService;
    private final CreateWorkflowTaskFactory createWorkflowTaskFactory;
    private final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository;

    @Autowired
    public DistrictWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
            final DistrictCommandFromApiJsonDeserializer fromApiJsonDeserializer, final DistrictRepositoryWrapper districtRepository,
            final StateRepositoryWrapper stateRepository, final ConfigurationDomainService configurationDomainService,
            final CreateWorkflowTaskFactory createWorkflowTaskFactory,
            final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository) {
        this.context = context;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.districtRepository = districtRepository;
        this.stateRepository = stateRepository;
        this.configurationDomainService = configurationDomainService;
        this.createWorkflowTaskFactory = createWorkflowTaskFactory;
        this.taskConfigEntityTypeMappingRepository = taskConfigEntityTypeMappingRepository;
    }

    @Override
    @Transactional
    public CommandProcessingResult createDistrict(final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            this.fromApiJsonDeserializer.validateForCreate(command.json());
            final Long stateId = command.longValueOfParameterNamed(DistrictApiConstants.stateIdParamName);
            final State state = this.stateRepository.findOneWithNotFoundDetection(stateId);
            final District district = District.create(command, state);
            this.districtRepository.save(district);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(district.getId()) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDistrictDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    @Transactional
    public CommandProcessingResult updateDistrict(final Long districtId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            this.fromApiJsonDeserializer.validateForUpdate(command.json());
            final District district = this.districtRepository.findOneWithNotFoundDetection(districtId);
            final Map<String, Object> changes = district.update(command);
            if (changes.containsKey(DistrictApiConstants.stateIdParamName)) {
                final Long stateId = command.longValueOfParameterNamed(DistrictApiConstants.stateIdParamName);
                final State state = this.stateRepository.findOneWithNotFoundDetection(stateId);
                district.updateState(state);
            }
            if (!changes.isEmpty()) {
                this.districtRepository.saveAndFlush(district);
            }
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(district.getId()) //
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDistrictDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    @Transactional
    public CommandProcessingResult activateDistrict(final Long districtId, final JsonCommand command) {
        try {
            final AppUser currentUser = this.context.authenticatedUser();
            final District district = this.districtRepository.findOneWithNotFoundDetection(districtId);
            validateIsDistrictInPendingStatus(district);
            final Map<String, Object> changes = district.activate(currentUser);
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(district.getId()) //
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDistrictDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    @Transactional
    public CommandProcessingResult rejectDistrict(final Long districtId, final JsonCommand command) {
        try {
            final AppUser currentUser = this.context.authenticatedUser();
            final District district = this.districtRepository.findOneWithNotFoundDetection(districtId);
            validateIsDistrictInPendingStatus(district);
            final Map<String, Object> changes = district.reject(currentUser);
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(district.getId()) //
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDistrictDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    private void validateIsDistrictInPendingStatus(final District district) {
        if (!district.isPending()) { throw new GeneralPlatformDomainRuleException("error.msg.district.is.not.in.pending.status",
                "District with identifier `" + district.getId() + "` is not in pending status", district.getId()); }
    }

    private void handleDistrictDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

        final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("UQ_sid_iso_district_code_name")) {
            final String districtCode = command.stringValueOfParameterNamed(DistrictApiConstants.districtCodeParamName);
            final String districtName = command.stringValueOfParameterNamed(DistrictApiConstants.districtNameParamName);
            final Long stateId = command.longValueOfParameterNamed(DistrictApiConstants.stateIdParamName);
            throw new PlatformDataIntegrityException("error.msg.district.duplicate.entry", "Duplicate entry for district", districtName,
                    districtCode, stateId);
        }

        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.office.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    @Override
    @Transactional
    public CommandProcessingResult intiateDistrictWorkflow(final Long districtId, final JsonCommand command) {
        try {
            final AppUser currentUser = this.context.authenticatedUser();
            final District district = this.districtRepository.findOneWithNotFoundDetection(districtId);
            validateIsDistrictInPendingStatus(district);
            if (this.configurationDomainService.isWorkFlowEnabled()) {
                final TaskConfigEntityTypeMapping taskConfigEntityTypeMapping = this.taskConfigEntityTypeMappingRepository
                        .findOneByEntityTypeAndEntityId(TaskConfigEntityType.DISTRICTONBOARDING.getValue(), -1L);
                if(taskConfigEntityTypeMapping != null) {
                    WorkflowDTO workflowDTO = new WorkflowDTO(district, currentUser.getOffice());
                    this.createWorkflowTaskFactory.create(TaskConfigEntityType.DISTRICTONBOARDING).createWorkFlow(workflowDTO);    
                }
            }
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(district.getId()) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDistrictDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }
}
