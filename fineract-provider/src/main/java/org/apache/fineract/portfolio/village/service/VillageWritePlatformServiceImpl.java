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
package org.apache.fineract.portfolio.village.service;

import java.util.Map;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandProcessingService;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.office.domain.OfficeRepository;
import org.apache.fineract.organisation.office.exception.OfficeNotFoundException;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.group.domain.GroupRepository;
import org.apache.fineract.portfolio.group.exception.CenterNotFoundException;
import org.apache.fineract.portfolio.village.api.VillageTypeApiConstants;
import org.apache.fineract.portfolio.village.domain.Village;
import org.apache.fineract.portfolio.village.domain.VillageRepository;
import org.apache.fineract.portfolio.village.domain.VillageRepositoryWrapper;
import org.apache.fineract.portfolio.village.exception.DuplicateVillageNameException;
import org.apache.fineract.portfolio.village.exception.InvalidVillageStateTransitionException;
import org.apache.fineract.portfolio.village.exception.VillageMustBePendingToBeDeletedException;
import org.apache.fineract.portfolio.village.serialization.VillageDataValidator;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.kyc.address.api.AddressApiConstants;
import com.finflux.kyc.address.data.AddressEntityTypeEnums;
import com.finflux.kyc.address.service.AddressWritePlatformService;

@Service
public class VillageWritePlatformServiceImpl implements VillageWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(VillageWritePlatformServiceImpl.class);
    
    private final PlatformSecurityContext context;
    private final VillageDataValidator fromApiJsonDeserializer;
    private final VillageRepositoryWrapper villageRepository;
    private final OfficeRepository officeRepository;
    private final GroupRepository centerRepository;
    private final CommandProcessingService commandProcessingService;
    private final VillageRepository villageRepo;
    private final AddressWritePlatformService addressWritePlatformService;
    @Autowired
    public VillageWritePlatformServiceImpl(PlatformSecurityContext context, VillageDataValidator fromApiJsonDeserializer, VillageRepositoryWrapper villageRepository, 
            OfficeRepository officeRepository, GroupRepository centerRepository, CommandProcessingService commandProcessingService,VillageRepository villageRepo, final AddressWritePlatformService addressWritePlatformService) {

        this.context = context;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.villageRepository = villageRepository;
        this.officeRepository = officeRepository;
        this.centerRepository = centerRepository;
        this.commandProcessingService = commandProcessingService;
        this.villageRepo=villageRepo;
        this.addressWritePlatformService = addressWritePlatformService;
    }
    
    
    @Transactional
    @Override
    public CommandProcessingResult createVillage(final JsonCommand command) {

        try{
            final AppUser currentUser = this.context.authenticatedUser();
            this.fromApiJsonDeserializer.validateForCreateVillage(command);
            
            final Long officeId = command.longValueOfParameterNamed(VillageTypeApiConstants.officeIdParamName);
            
            final Office villageOffice = this.officeRepository.findOne(officeId);
            if (villageOffice == null) {
                throw  new OfficeNotFoundException(officeId);
            }
            
            final String villageName = command.stringValueOfParameterNamed(VillageTypeApiConstants.villageNameParamName);
            
            Long count = command.longValueOfParameterNamed(VillageTypeApiConstants.countParamName);
            if (count == null) {
                count = 0L;
            }
            final LocalDate activationDate = command.localDateValueOfParameterNamed(VillageTypeApiConstants.activationDateParamName);
            
            validateOfficeOpeningDateIsAfterVillageOpeningDate(villageOffice, activationDate);
            
            final Long centerId = command.longValueOfParameterNamed(VillageTypeApiConstants.centerIdParamName);
            
            Group centerOfVillage = null;
            if (centerId != null) {
                centerOfVillage = this.centerRepository.findOne(centerId);
                if (centerOfVillage == null) { throw new CenterNotFoundException(centerId); }
            }
               
            final boolean active = command.booleanPrimitiveValueOfParameterNamed(VillageTypeApiConstants.activeParamName);
            LocalDate submittedOnDate = DateUtils.getLocalDateOfTenant();
            if (active && submittedOnDate.isAfter(activationDate)) {
                submittedOnDate = activationDate;
            }
            
            if (command.hasParameter(VillageTypeApiConstants.submittedOnDateParamName)) {
                submittedOnDate = command.localDateValueOfParameterNamed(VillageTypeApiConstants.submittedOnDateParamName);
            }
            
            final Village newVillage = Village.newVillage(villageOffice, villageName, count, currentUser, active, activationDate, submittedOnDate, command);
            
            if (centerOfVillage != null) {
                newVillage.setCenter(centerOfVillage);
            }
            
            boolean rollbackTransaction = false;
            if (newVillage.isActive()) {
                final CommandWrapper commandWrapper = new CommandWrapperBuilder().activateVillage(null).build();
                rollbackTransaction = this.commandProcessingService.validateCommand(commandWrapper, currentUser);
            }
            Integer VillageNameCount=this.villageRepo.retrieveVillageNameCount(villageName,officeId);
            if(VillageNameCount!=0){
                throw new DuplicateVillageNameException(villageName);
            }
            this.villageRepository.save(newVillage);
            
            /**
             * Call Address Service
             */
            if (newVillage != null && newVillage.getId() != null && command.parameterExists(AddressApiConstants.addressesParamName)) {
                final AddressEntityTypeEnums entityType = AddressEntityTypeEnums.VILLAGES;
                this.addressWritePlatformService.createOrUpdateAddress(entityType, newVillage.getId(), command);
            }

            
            return new CommandProcessingResultBuilder() //
                        .withCommandId(command.commandId()) //
                        .withOfficeId(villageOffice.getId()) // 
                        .withSubEntityId(centerId) //
                        .withEntityId(newVillage.getId()) //
                        .setRollbackTransaction(rollbackTransaction) //
                        .build();
        }catch(final DataIntegrityViolationException dive) {
            handleVillageDataIntegrityIssues(command, dive);
            return CommandProcessingResult.empty(); 
        }
    }

    private void handleVillageDataIntegrityIssues(JsonCommand command, DataIntegrityViolationException dive) {

        final Throwable realCause = dive.getMostSpecificCause();
        String errorMessageForUser = null;
        String errorMessageForMachine = null;
        
        if (realCause.getMessage().contains("villageName")) {
            final String name = command.stringValueOfParameterNamed(VillageTypeApiConstants.villageNameParamName);
            errorMessageForUser = "village with name" + name + " already exists.";
            errorMessageForMachine = "error.msg.village.duplicate.name";
            throw new PlatformDataIntegrityException(errorMessageForMachine, errorMessageForUser, VillageTypeApiConstants.villageNameParamName, name);
        } 
        logger.error(dive.getMessage(), dive);
        throw new PlatformDataIntegrityException("error.msg.village.unknown.data.integrity.issue", "Unknown data integrity issue with resource."); 
    }

    private void validateOfficeOpeningDateIsAfterVillageOpeningDate(Office villageOffice, LocalDate activationDate) {

        if (activationDate != null && villageOffice.getOpeningLocalDate().isAfter(activationDate)) {
            
            final String errorMessage = "activation date should be greater than or equal to the parent Office's creation date " + activationDate.toString();
            
            throw new InvalidVillageStateTransitionException("activate.date", "cannot.be. before.office.activation.date", errorMessage, activationDate, 
                    villageOffice.getOpeningLocalDate());
        }
    }
    
    @Transactional
    @Override
    public CommandProcessingResult updateVillage(final Long villageId, final JsonCommand command) {

        try {
            this.fromApiJsonDeserializer.validateForUpdateVillage(command);
            
            final Village villageForUpdate = this.villageRepository.findOneWithNotFoundDetection(villageId);
            final Office officeId = villageForUpdate.getOffice();
            final String villageHierarchy = villageForUpdate.getOffice().getHierarchy();
            
            this.context.validateAccessRights(villageHierarchy);
            
            final LocalDate activationDate = command.localDateValueOfParameterNamed(VillageTypeApiConstants.activationDateParamName);
            validateOfficeOpeningDateIsAfterVillageOpeningDate(officeId, activationDate);
            
            final Map<String, Object> changes = villageForUpdate.update(command);
            
            if (!changes.isEmpty()) {
                this.villageRepository.saveAndFlush(villageForUpdate);
            }
            
            return new CommandProcessingResultBuilder() //
            .withCommandId(command.commandId()) //
            .withOfficeId(villageForUpdate.officeId()) //
            .withGroupId(villageForUpdate.getId()) //
            .withEntityId(villageForUpdate.getId()) //
            .with(changes) //
            .build();
            
        } catch (final DataIntegrityViolationException e) {
            handleVillageDataIntegrityIssues(command, e);
            return CommandProcessingResult.empty();
        }
    }


    @Transactional
    @Override
    public CommandProcessingResult deleteVillage(Long villageId) {

        final Village village = this.villageRepository.findOneWithNotFoundDetection(villageId);
        if (village.isNotPending()) { throw new VillageMustBePendingToBeDeletedException(villageId);
        }
        
        this.villageRepository.delete(village);
        
        return new CommandProcessingResultBuilder() //
                .withOfficeId(village.officeId()) //
                .withEntityId(villageId) //
                .build();
    }
    
    @Transactional
    @Override
    public CommandProcessingResult activateVillage(final Long villageId, final JsonCommand command) {

        try {
            this.fromApiJsonDeserializer.validateForActivation(command, VillageTypeApiConstants.VILLAGE_RESOURCE_NAME);

            final AppUser currentUser = this.context.authenticatedUser();

            final Village village = this.villageRepository.findOneWithNotFoundDetection(villageId);

            final LocalDate activationDate = command.localDateValueOfParameterNamed("activatedOnDate");

            validateOfficeOpeningDateIsAfterVillageOpeningDate(village.getOffice(), activationDate);
            village.activate(currentUser, activationDate);

            this.villageRepository.saveAndFlush(village);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withOfficeId(village.officeId()) //
                    .withEntityId(villageId) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleVillageDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }
    
}
