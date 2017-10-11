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
package org.apache.fineract.portfolio.village.serialization;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.village.api.VillageTypeApiConstants;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;


@Component
public class VillageDataValidator {

    private FromJsonHelper fromApiJsonHelper;

    @Autowired
    public VillageDataValidator(FromJsonHelper fromApiJsonHelper) {

        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    private void throwExceptionIfValidationWarningsExist(List<ApiParameterError> dataValidationErrors) {

        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
    }
    
    public void validateForCreateVillage(final JsonCommand command) {
        
        final String json = command.json();
        
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }
        
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, VillageTypeApiConstants.VILLAGE_REQUEST_DATA_PARAMETERS);
        
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                    .resource(VillageTypeApiConstants.VILLAGE_RESOURCE_NAME);
        
        final JsonElement element = command.parsedJson();
        
        final Long officeId = this.fromApiJsonHelper.extractLongNamed(VillageTypeApiConstants.officeIdParamName, element);
        baseDataValidator.reset().parameter(VillageTypeApiConstants.officeIdParamName).value(officeId).notNull().longGreaterThanZero();
        
        final String name = this.fromApiJsonHelper.extractStringNamed(VillageTypeApiConstants.villageNameParamName, element);
        baseDataValidator.reset().parameter(VillageTypeApiConstants.villageNameParamName).value(name).notNull();
        
        final Boolean active = this.fromApiJsonHelper.extractBooleanNamed(VillageTypeApiConstants.activeParamName, element);
        if (active != null) {
            if (active.booleanValue()) {
                final LocalDate joinedDate = this.fromApiJsonHelper.extractLocalDateNamed(VillageTypeApiConstants.activationDateParamName, element);
                baseDataValidator.reset().parameter(VillageTypeApiConstants.activationDateParamName).value(joinedDate).notNull();
            }else {
                final boolean isPendingApprovalEnabled = true;
                if (!isPendingApprovalEnabled) {
                    baseDataValidator.reset().parameter(VillageTypeApiConstants.activeParamName).failWithCode(".pending.status.not.allowed");
                }
            }
        }else {
            baseDataValidator.reset().parameter(VillageTypeApiConstants.activeParamName).value(active).trueOrFalseRequired(false);
        }
        
        if (this.fromApiJsonHelper.parameterExists(VillageTypeApiConstants.submittedOnDateParamName, element)) {
            final LocalDate submittedOnDate = this.fromApiJsonHelper.extractLocalDateNamed(VillageTypeApiConstants.submittedOnDateParamName, element);
            baseDataValidator.reset().parameter(VillageTypeApiConstants.submittedOnDateParamName).value(submittedOnDate).notNull();
        }
        if (this.fromApiJsonHelper.parameterExists(VillageTypeApiConstants.pincodeParamName, element)) {
            final Long pincode = this.fromApiJsonHelper.extractLongNamed(VillageTypeApiConstants.pincodeParamName, element);
            baseDataValidator.reset().parameter(VillageTypeApiConstants.pincodeParamName).value(pincode).longGreaterThanZero().notNull();
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
    
    public void validateForUpdateVillage(final JsonCommand command) {
        
        final String json = command.json();
        
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }
        
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, VillageTypeApiConstants.VILLAGE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                        .resource(VillageTypeApiConstants.VILLAGE_RESOURCE_NAME);
        
        final JsonElement element = command.parsedJson();
        
        final String name = this.fromApiJsonHelper.extractStringNamed(VillageTypeApiConstants.villageNameParamName, element);
        baseDataValidator.reset().parameter(VillageTypeApiConstants.villageNameParamName).value(name).notNull();
        
        if (this.fromApiJsonHelper.parameterExists(VillageTypeApiConstants.externalIdParamName, element)) {
            final String externalId = this.fromApiJsonHelper.extractStringNamed(VillageTypeApiConstants.externalIdParamName, element);
            baseDataValidator.reset().parameter(VillageTypeApiConstants.externalIdParamName).value(externalId);
        }
        
        final Boolean active = this.fromApiJsonHelper.extractBooleanNamed(VillageTypeApiConstants.activeParamName, element);
        if (active != null) {
            if (active.booleanValue()) {
                final LocalDate joinedDate = this.fromApiJsonHelper.extractLocalDateNamed(VillageTypeApiConstants.activationDateParamName, element);
                baseDataValidator.reset().parameter(VillageTypeApiConstants.activationDateParamName).value(joinedDate).notNull();
            }
        }
        if (this.fromApiJsonHelper.parameterExists(VillageTypeApiConstants.pincodeParamName, element)) {
            final Long pincode = this.fromApiJsonHelper.extractLongNamed(VillageTypeApiConstants.pincodeParamName, element);
            baseDataValidator.reset().parameter(VillageTypeApiConstants.pincodeParamName).value(pincode).longGreaterThanZero().notNull();
        }
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
    
    public void validateForActivation(final JsonCommand command, final String resourceName) {
        final String json = command.json();

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, VillageTypeApiConstants.ACTIVATION_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource(resourceName);

        final JsonElement element = command.parsedJson();

        final LocalDate activationDate = this.fromApiJsonHelper.extractLocalDateNamed(VillageTypeApiConstants.activationDateParamName,
                element);
        baseDataValidator.reset().parameter(VillageTypeApiConstants.activationDateParamName).value(activationDate).notNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
    
    public void validateForRejectMultipleVillages(final JsonCommand command) {

        final String json = command.json();
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, VillageTypeApiConstants.BULK_REJECT_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(VillageTypeApiConstants.VILLAGE_RESOURCE_NAME);

        final String[] villages = command.arrayValueOfParameterNamed(VillageTypeApiConstants.villagesParamName);
        baseDataValidator.reset().parameter(VillageTypeApiConstants.villagesParamName).value(villages).arrayNotEmpty();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
}
