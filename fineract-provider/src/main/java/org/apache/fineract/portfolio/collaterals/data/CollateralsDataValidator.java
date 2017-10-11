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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.collaterals.api.CollateralsApiConstants;
import org.apache.fineract.portfolio.collaterals.api.CollateralsApiConstants.COLLATERALS_TYPE_CLASSIFIER_PARAMS;
import org.apache.fineract.portfolio.collaterals.exception.QualityStandardAttatchedToPledge;
import org.apache.fineract.portfolio.collaterals.exception.QualityStandardsNullPriceException;
import org.apache.fineract.portfolio.collaterals.service.PledgeReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class CollateralsDataValidator {
    
    private final FromJsonHelper fromApiJsonHelper;
    private final PledgeReadPlatformService pledgeReadPlatformService;
    
    @Autowired
    public CollateralsDataValidator(final FromJsonHelper fromApiJsonHelper, final PledgeReadPlatformService pledgeReadPlatformService){
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.pledgeReadPlatformService = pledgeReadPlatformService;
    }

    public void validateForCreate(final JsonCommand command) {
        
        final String json = command.json();
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
        
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, CollateralsApiConstants.COLLATERALS_REQUEST_DATA_PARAMETERS);
        
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(CollateralsApiConstants.COLLATERALS_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();
        
        final String name = this.fromApiJsonHelper.extractStringNamed(CollateralsApiConstants.nameParamName, element);
        baseDataValidator.reset().parameter(CollateralsApiConstants.nameParamName).value(name).notNull().notExceedingLengthOf(100);
        
        if (this.fromApiJsonHelper.parameterExists(CollateralsApiConstants.descriptionParamName, element)) {
            final String description = this.fromApiJsonHelper.extractStringNamed(CollateralsApiConstants.descriptionParamName, element);
            baseDataValidator.reset().parameter(CollateralsApiConstants.descriptionParamName).value(description).ignoreIfNull();
        }
        
        final Integer typeClassifier = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(CollateralsApiConstants.typeClassifierParamName, element);
        baseDataValidator.reset().parameter(CollateralsApiConstants.typeClassifierParamName).value(typeClassifier).notNull().isOneOfTheseValues(
                COLLATERALS_TYPE_CLASSIFIER_PARAMS.PRECIOUS_STONE.getValue(),COLLATERALS_TYPE_CLASSIFIER_PARAMS.PRECIOUS_METAL.getValue(), 
                COLLATERALS_TYPE_CLASSIFIER_PARAMS.OTHERS.getValue());
        
        final BigDecimal baseUnitPrice = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(CollateralsApiConstants.baseUnitPriceParamName, element);
        baseDataValidator.reset().parameter(CollateralsApiConstants.baseUnitPriceParamName).value(baseUnitPrice).notNull().positiveAmount();
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
        
    }

    public void validateForUpdate(JsonCommand command) {
        
        final String json = command.json();
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
        
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, CollateralsApiConstants.COLLATERALS_REQUEST_DATA_PARAMETERS);
        
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(CollateralsApiConstants.COLLATERALS_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();
        
        if (this.fromApiJsonHelper.parameterExists(CollateralsApiConstants.nameParamName, element)){
            final String name = this.fromApiJsonHelper.extractStringNamed(CollateralsApiConstants.nameParamName, element);
            baseDataValidator.reset().parameter(CollateralsApiConstants.nameParamName).value(name).notNull().notExceedingLengthOf(100);
        }
                
        if (this.fromApiJsonHelper.parameterExists(CollateralsApiConstants.descriptionParamName, element)) {
            final String description = this.fromApiJsonHelper.extractStringNamed(CollateralsApiConstants.descriptionParamName, element);
            baseDataValidator.reset().parameter(CollateralsApiConstants.descriptionParamName).value(description).ignoreIfNull();
        }
        
        if(this.fromApiJsonHelper.parameterExists(CollateralsApiConstants.typeClassifierParamName, element)){
            final Integer typeClassifier = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(CollateralsApiConstants.typeClassifierParamName, element);
            baseDataValidator.reset().parameter(CollateralsApiConstants.typeClassifierParamName).value(typeClassifier).notNull().isOneOfTheseValues(
                    COLLATERALS_TYPE_CLASSIFIER_PARAMS.PRECIOUS_STONE.getValue(),COLLATERALS_TYPE_CLASSIFIER_PARAMS.PRECIOUS_METAL.getValue(), 
                    COLLATERALS_TYPE_CLASSIFIER_PARAMS.OTHERS.getValue());
            
        }
        
        if(this.fromApiJsonHelper.parameterExists(CollateralsApiConstants.baseUnitPriceParamName, element)){
            final BigDecimal baseUnitPrice = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(CollateralsApiConstants.baseUnitPriceParamName, element);
            baseDataValidator.reset().parameter(CollateralsApiConstants.baseUnitPriceParamName).value(baseUnitPrice).notNull().positiveAmount();
        }
        
       throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
    
    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            //
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
    }
    
    public void validateForCreateQualityStandards(JsonCommand command) {
        
        final String json = command.json();
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
        
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, CollateralsApiConstants.QUALITY_STANDARDS_REQUEST_DATA_PARAMETERS);
        
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(CollateralsApiConstants.COLLATERALS_QUALITY_STANDARDS_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();
        
        final String name = this.fromApiJsonHelper.extractStringNamed(CollateralsApiConstants.nameParamName, element);
        baseDataValidator.reset().parameter(CollateralsApiConstants.nameParamName).value(name).notNull().notExceedingLengthOf(100);
        
        if (this.fromApiJsonHelper.parameterExists(CollateralsApiConstants.descriptionParamName, element)) {
            final String description = this.fromApiJsonHelper.extractStringNamed(CollateralsApiConstants.descriptionParamName, element);
            baseDataValidator.reset().parameter(CollateralsApiConstants.descriptionParamName).value(description).ignoreIfNull();
        }
                
        final BigDecimal absolutePrice = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(CollateralsApiConstants.absolutePriceParamName, element);
        baseDataValidator.reset().parameter(CollateralsApiConstants.absolutePriceParamName).value(absolutePrice).ignoreIfNull().positiveAmount();
        
        final BigDecimal percentagePrice = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(CollateralsApiConstants.percentagePriceParamName, element);
        baseDataValidator.reset().parameter(CollateralsApiConstants.percentagePriceParamName).value(percentagePrice).ignoreIfNull().positiveAmount();
        System.out.println(percentagePrice+ " "+ absolutePrice);
        if(percentagePrice==null && absolutePrice==null){
        	throw new QualityStandardsNullPriceException("validation.msg.validation.errors.exist","Both Abolute and Percentage price can not be empty.",dataValidationErrors);
        }
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

	public void validateForUpdateQualityStandards(final JsonCommand command) {
		final String json = command.json();
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
        
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, CollateralsApiConstants.QUALITY_STANDARDS_UPDATE_REQUEST_DATA_PARAMETERS);
        
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(CollateralsApiConstants.COLLATERALS_QUALITY_STANDARDS_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();
        
        final Long qualityStandardId = this.fromApiJsonHelper.extractLongNamed(CollateralsApiConstants.idParamName, element);
        Integer numberOfCollateralDetailsData = this.pledgeReadPlatformService.retrieveNumberOfCollateralDetailsByQualityStandardId(qualityStandardId);
        if(numberOfCollateralDetailsData>0){
        	throw new QualityStandardAttatchedToPledge();
        }
        if (this.fromApiJsonHelper.parameterExists(CollateralsApiConstants.nameParamName, element)){
            final String name = this.fromApiJsonHelper.extractStringNamed(CollateralsApiConstants.nameParamName, element);
            baseDataValidator.reset().parameter(CollateralsApiConstants.nameParamName).value(name).notNull().notExceedingLengthOf(100);
        }
                
        if (this.fromApiJsonHelper.parameterExists(CollateralsApiConstants.descriptionParamName, element)) {
            final String description = this.fromApiJsonHelper.extractStringNamed(CollateralsApiConstants.descriptionParamName, element);
            baseDataValidator.reset().parameter(CollateralsApiConstants.descriptionParamName).value(description).ignoreIfNull();
        }
        
        if(this.fromApiJsonHelper.parameterExists(CollateralsApiConstants.percentagePriceParamName, element)){
            final BigDecimal percentagePrice = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(CollateralsApiConstants.percentagePriceParamName, element);
            baseDataValidator.reset().parameter(CollateralsApiConstants.percentagePriceParamName).value(percentagePrice).ignoreIfNull().positiveAmount();
        }
        
        if(this.fromApiJsonHelper.parameterExists(CollateralsApiConstants.absolutePriceParamName, element)){
            final BigDecimal absolutePrice = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(CollateralsApiConstants.absolutePriceParamName, element);
            baseDataValidator.reset().parameter(CollateralsApiConstants.absolutePriceParamName).value(absolutePrice).ignoreIfNull().positiveAmount();
        }
        
       throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}
    
	public void validateForDeleteQualityStandards(final Long id) {
		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(CollateralsApiConstants.COLLATERALS_QUALITY_STANDARDS_RESOURCE_NAME);
        Integer numberOfCollateralDetailsData = this.pledgeReadPlatformService.retrieveNumberOfCollateralDetailsByQualityStandardId(id);
        if(numberOfCollateralDetailsData>0){
        	throw new QualityStandardAttatchedToPledge();
        }
        
       throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}


}
