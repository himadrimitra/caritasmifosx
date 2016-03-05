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
package org.apache.fineract.portfolio.collaterals.data;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.collaterals.api.PledgeApiConstants;
import org.apache.fineract.portfolio.collaterals.api.PledgeApiConstants.PLEDGE_STATUS_PARAMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class PledgeDataValidator {
    
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public PledgeDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final JsonCommand command) {
     
        final String json = command.json();
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
        
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, PledgeApiConstants.PLEDGE_REQUEST_DATA_PARAMETERS);
        
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
        .resource(PledgeApiConstants.COLLATERAL_PLEDGE_RESOURCE_NAME);
        
        final JsonElement element = command.parsedJson();
        
        if (this.fromApiJsonHelper.parameterExists(PledgeApiConstants.clientIdParamName, element)) {
            final Long clientId = this.fromApiJsonHelper.extractLongNamed(PledgeApiConstants.clientIdParamName, element);
            baseDataValidator.reset().parameter(PledgeApiConstants.clientIdParamName).value(clientId).ignoreIfNull();
        }
        
        if (this.fromApiJsonHelper.parameterExists(PledgeApiConstants.loanIdParamName, element)) {
            final Long loanId = this.fromApiJsonHelper.extractLongNamed(PledgeApiConstants.loanIdParamName, element);
            baseDataValidator.reset().parameter(PledgeApiConstants.loanIdParamName).value(loanId).ignoreIfNull();
        }
        
        if (this.fromApiJsonHelper.parameterExists(PledgeApiConstants.sealNumberParamName, element)) {
            final Long sealNumber = this.fromApiJsonHelper.extractLongNamed(PledgeApiConstants.sealNumberParamName, element);
            baseDataValidator.reset().parameter(PledgeApiConstants.sealNumberParamName).value(sealNumber).ignoreIfNull().integerGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(PledgeApiConstants.statusParamName, element)) {
            final Integer status = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(PledgeApiConstants.statusParamName, element);
            baseDataValidator.reset().parameter(PledgeApiConstants.statusParamName).value(status).notNull().isOneOfTheseValues(PLEDGE_STATUS_PARAMS.INITIATE_PLEDGE.getValue(),
                    PLEDGE_STATUS_PARAMS.ACTIVE_PLEDGE.getValue(), PLEDGE_STATUS_PARAMS.CLOSE_PLEDGE.getValue());
        }
        
        final BigDecimal systemValue = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(PledgeApiConstants.systemValueParamName, element);
        baseDataValidator.reset().parameter(PledgeApiConstants.systemValueParamName).value(systemValue).notNull().positiveAmount();
        
        final BigDecimal userValue = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(PledgeApiConstants.userValueParamName, element);
        baseDataValidator.reset().parameter(PledgeApiConstants.userValueParamName).value(userValue).notNull().positiveAmount();
        
        //Collaterals Details
        if (element.isJsonArray() && this.fromApiJsonHelper.parameterExists(PledgeApiConstants.collateralDetailsParamName, element)) {
            final JsonArray collateralsDetails = element.getAsJsonArray();
            for(int i=0 ; i<collateralsDetails.size(); i++){
                
                final JsonObject collateralDetail = collateralsDetails.getAsJsonObject();
                final String arrayObjectJson = this.fromApiJsonHelper.toJson(collateralDetail);
                this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, arrayObjectJson, PledgeApiConstants.COLLATERAL_DETAILS_REQUEST_DATA_PARAMETERS);
                
                final Long collateralId = this.fromApiJsonHelper.extractLongNamed(PledgeApiConstants.collateralIdParamName, collateralDetail);
                baseDataValidator.reset().parameter(PledgeApiConstants.collateralIdParamName).parameterAtIndexArray(PledgeApiConstants.collateralIdParamName, i).value(collateralId).notNull()
                        .integerGreaterThanZero();
                
                final Long qualityStandard = this.fromApiJsonHelper.extractLongNamed(PledgeApiConstants.qualityStandardIdParamName, collateralDetail);
                baseDataValidator.reset().parameter(PledgeApiConstants.qualityStandardIdParamName).parameterAtIndexArray(PledgeApiConstants.qualityStandardIdParamName, i).value(qualityStandard).notNull()
                        .integerGreaterThanZero();
                
                if (this.fromApiJsonHelper.parameterExists(PledgeApiConstants.descriptionParamName, element)) {
                    final String description = this.fromApiJsonHelper.extractStringNamed(PledgeApiConstants.descriptionParamName, element);
                    baseDataValidator.reset().parameter(PledgeApiConstants.descriptionParamName).parameterAtIndexArray(PledgeApiConstants.descriptionParamName, i).value(description).ignoreIfNull();
                }
                
                if (this.fromApiJsonHelper.parameterExists(PledgeApiConstants.grossWeightParamName, element)) {
                    final BigDecimal grossWeight = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(PledgeApiConstants.grossWeightParamName, element);
                    baseDataValidator.reset().parameter(PledgeApiConstants.grossWeightParamName).parameterAtIndexArray(PledgeApiConstants.grossWeightParamName, i).value(grossWeight).ignoreIfNull().positiveAmount();
                }
                
                if (this.fromApiJsonHelper.parameterExists(PledgeApiConstants.netWeightParamName, element)) {
                    final BigDecimal netWeight = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(PledgeApiConstants.netWeightParamName, element);
                    baseDataValidator.reset().parameter(PledgeApiConstants.netWeightParamName).parameterAtIndexArray(PledgeApiConstants.netWeightParamName, i).value(netWeight).ignoreIfNull().positiveAmount();
                }
                
                final BigDecimal systemPrice = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(PledgeApiConstants.systemPriceParamName, collateralDetail);
                baseDataValidator.reset().parameter(PledgeApiConstants.systemPriceParamName).parameterAtIndexArray(PledgeApiConstants.systemPriceParamName, i).value(systemPrice).notNull()
                        .positiveAmount();
                
                final BigDecimal userPrice = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(PledgeApiConstants.userPriceParamName, collateralDetail);
                baseDataValidator.reset().parameter(PledgeApiConstants.userPriceParamName).parameterAtIndexArray(PledgeApiConstants.userPriceParamName, i).value(userPrice).notNull()
                        .positiveAmount();
            }
                   
        }
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
        
    }
    
    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            //
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
    }

    public void validateForUpdate(final JsonCommand command) {
        
        final String json = command.json();
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
        
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, PledgeApiConstants.PLEDGE_UPDATE_REQUEST_DATA_PARAMETERS);
        
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
        .resource(PledgeApiConstants.COLLATERAL_PLEDGE_RESOURCE_NAME);
        
        final JsonElement element = command.parsedJson();
        boolean atleastOneParameterMustBePassedForUpdate = false;
        
        if (this.fromApiJsonHelper.parameterExists(PledgeApiConstants.clientIdParamName, element)) {
            atleastOneParameterMustBePassedForUpdate = true;
            final Long clientId = this.fromApiJsonHelper.extractLongNamed(PledgeApiConstants.clientIdParamName, element);
            baseDataValidator.reset().parameter(PledgeApiConstants.clientIdParamName).value(clientId).notNull().longGreaterThanZero();
        }
        
        if (this.fromApiJsonHelper.parameterExists(PledgeApiConstants.loanIdParamName, element)) {
            atleastOneParameterMustBePassedForUpdate = true;
            final Long loanId = this.fromApiJsonHelper.extractLongNamed(PledgeApiConstants.loanIdParamName, element);
            baseDataValidator.reset().parameter(PledgeApiConstants.loanIdParamName).value(loanId).notNull().longGreaterThanZero();
        }
        
        
        if (this.fromApiJsonHelper.parameterExists(PledgeApiConstants.sealNumberParamName, element)) {
            atleastOneParameterMustBePassedForUpdate = true;
            final Long sealNumber = this.fromApiJsonHelper.extractLongNamed(PledgeApiConstants.sealNumberParamName, element);
            baseDataValidator.reset().parameter(PledgeApiConstants.sealNumberParamName).value(sealNumber).notNull().longGreaterThanZero();
        }       
        
        if (this.fromApiJsonHelper.parameterExists(PledgeApiConstants.statusParamName, element)) {
            atleastOneParameterMustBePassedForUpdate = true;
            final Integer status = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(PledgeApiConstants.statusParamName, element);
            baseDataValidator.reset().parameter(PledgeApiConstants.statusParamName).value(status).notNull().isOneOfTheseValues(PLEDGE_STATUS_PARAMS.INITIATE_PLEDGE.getValue(),
                    PLEDGE_STATUS_PARAMS.ACTIVE_PLEDGE.getValue(), PLEDGE_STATUS_PARAMS.CLOSE_PLEDGE.getValue());
        }
        
        if (this.fromApiJsonHelper.parameterExists(PledgeApiConstants.userValueParamName, element)) {
            atleastOneParameterMustBePassedForUpdate = true;
            final BigDecimal userValue = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(PledgeApiConstants.userValueParamName, element);
            baseDataValidator.reset().parameter(PledgeApiConstants.userValueParamName).value(userValue).notNull().positiveAmount();
        }
        
        // Update Collaterals Details
        if (element.isJsonArray() && this.fromApiJsonHelper.parameterExists(PledgeApiConstants.collateralDetailsParamName, element)) {
            final JsonArray collateralsDetails = element.getAsJsonArray();
            for(int i=0 ; i<collateralsDetails.size(); i++){
                
                final JsonObject collateralDetail = collateralsDetails.getAsJsonObject();
                final String arrayObjectJson = this.fromApiJsonHelper.toJson(collateralDetail);
                this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, arrayObjectJson, PledgeApiConstants.COLLATERAL_DETAILS_REQUEST_DATA_PARAMETERS);
                
                final Long collateralId = this.fromApiJsonHelper.extractLongNamed(PledgeApiConstants.collateralIdParamName, collateralDetail);
                baseDataValidator.reset().parameter(PledgeApiConstants.collateralIdParamName).parameterAtIndexArray(PledgeApiConstants.collateralIdParamName, i).value(collateralId).notNull()
                        .integerGreaterThanZero();
                
                final Long qualityStandard = this.fromApiJsonHelper.extractLongNamed(PledgeApiConstants.qualityStandardIdParamName, collateralDetail);
                baseDataValidator.reset().parameter(PledgeApiConstants.qualityStandardIdParamName).parameterAtIndexArray(PledgeApiConstants.qualityStandardIdParamName, i).value(qualityStandard).notNull()
                        .integerGreaterThanZero();
                
                if (this.fromApiJsonHelper.parameterExists(PledgeApiConstants.descriptionParamName, element)) {
                    final String description = this.fromApiJsonHelper.extractStringNamed(PledgeApiConstants.descriptionParamName, element);
                    baseDataValidator.reset().parameter(PledgeApiConstants.descriptionParamName).parameterAtIndexArray(PledgeApiConstants.descriptionParamName, i).value(description).ignoreIfNull();
                }
                
                if (this.fromApiJsonHelper.parameterExists(PledgeApiConstants.grossWeightParamName, element)) {
                    final BigDecimal grossWeight = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(PledgeApiConstants.grossWeightParamName, element);
                    baseDataValidator.reset().parameter(PledgeApiConstants.grossWeightParamName).parameterAtIndexArray(PledgeApiConstants.grossWeightParamName, i).value(grossWeight).ignoreIfNull().positiveAmount();
                }
                
                if (this.fromApiJsonHelper.parameterExists(PledgeApiConstants.netWeightParamName, element)) {
                    final BigDecimal netWeight = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(PledgeApiConstants.netWeightParamName, element);
                    baseDataValidator.reset().parameter(PledgeApiConstants.netWeightParamName).parameterAtIndexArray(PledgeApiConstants.netWeightParamName, i).value(netWeight).ignoreIfNull().positiveAmount();
                }
                
                final BigDecimal systemPrice = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(PledgeApiConstants.systemPriceParamName, collateralDetail);
                baseDataValidator.reset().parameter(PledgeApiConstants.systemPriceParamName).parameterAtIndexArray(PledgeApiConstants.systemPriceParamName, i).value(systemPrice).notNull()
                        .positiveAmount();
                
                final BigDecimal userPrice = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(PledgeApiConstants.userPriceParamName, collateralDetail);
                baseDataValidator.reset().parameter(PledgeApiConstants.userPriceParamName).parameterAtIndexArray(PledgeApiConstants.userPriceParamName, i).value(userPrice).notNull()
                        .positiveAmount();
            }
                   
        }
        
        if (!atleastOneParameterMustBePassedForUpdate) {
            final Object forceError = null;
            baseDataValidator.reset().anyOfNotNull(forceError);
        }
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
        
    }

    public void validateForClosePledge(final JsonCommand command) {
        
        final String json = command.json();
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
        
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, PledgeApiConstants.PLEDGE_CLOSE_REQUEST_DATA_PARAMETERS);
        
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
        .resource(PledgeApiConstants.COLLATERAL_PLEDGE_RESOURCE_NAME);
        
        final JsonElement element = command.parsedJson();
        
        final LocalDate closureDate = this.fromApiJsonHelper.extractLocalDateNamed(PledgeApiConstants.closureDateParamName, element);
        baseDataValidator.reset().parameter(PledgeApiConstants.closureDateParamName).value(closureDate).notNull();
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

}
