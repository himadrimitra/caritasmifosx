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
package org.apache.fineract.infrastructure.sms.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.fortuna.ical4j.model.property.RRule;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.sms.domain.SmsCampaignType;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class SmsCampaignValidator {

   

    public static final String RESOURCE_NAME = "sms";
    public static final String campaignName  = "campaignName";
    public static final String campaignType = "campaignType";
    public static final String runReportId = "runReportId";
    public static final String paramValue = "paramValue";
    public static final String message   = "message";
    public static final String activationDateParamName = "activationDate";
    public static final String recurrenceStartDate = "recurrenceStartDate";
    public static final String submittedOnDateParamName = "submittedOnDate";
    public static final String closureDateParamName = "closureDate";
    public static final String recurrenceParamName = "recurrence";
    public static final String statusParamName = "status";

    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";


    private final FromJsonHelper fromApiJsonHelper;


    public static final Set<String> supportedParams = new HashSet<>(Arrays.asList(campaignName, campaignType,localeParamName,dateFormatParamName,
            runReportId,paramValue,message,recurrenceStartDate,activationDateParamName,submittedOnDateParamName,closureDateParamName,recurrenceParamName));

    public static final Set<String> supportedParamsForUpdate = new HashSet<>(Arrays.asList(campaignName, campaignType,localeParamName,dateFormatParamName,
            runReportId,paramValue,message,recurrenceStartDate,activationDateParamName,recurrenceParamName));

    public static final Set<String> ACTIVATION_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(localeParamName,
            dateFormatParamName, activationDateParamName));

    public static final Set<String> CLOSE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(localeParamName,
            dateFormatParamName, closureDateParamName));

    public static final Set<String> PREVIEW_REQUEST_DATA_PARAMETERS= new HashSet<>(Arrays.asList(paramValue,message));

    @Autowired
    public SmsCampaignValidator(FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }


    public void validateCreate(String json){
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SmsCampaignValidator.supportedParams);
        
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SmsCampaignValidator.RESOURCE_NAME);
        
        final String campaignName =  this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.campaignName,element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.campaignName).value(campaignName).notBlank().notExceedingLengthOf(100);
        
        
        final Long campaignType = this.fromApiJsonHelper.extractLongNamed(SmsCampaignValidator.campaignType,element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.campaignType).value(campaignType).notNull().integerGreaterThanZero();

        if(campaignType.intValue() == SmsCampaignType.SCHEDULE.getValue()){
            final String recurrenceParamName =  this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.recurrenceParamName, element);
            baseDataValidator.reset().parameter(SmsCampaignValidator.recurrenceParamName).value(recurrenceParamName).notBlank();            
            validateRecurrenceRule(recurrenceParamName, SmsCampaignValidator.recurrenceParamName, baseDataValidator);
            final String recurrenceStartDate =  this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.recurrenceStartDate, element);
            baseDataValidator.reset().parameter(SmsCampaignValidator.recurrenceStartDate).value(recurrenceStartDate).notBlank();
        }

        final Long runReportId = this.fromApiJsonHelper.extractLongNamed(SmsCampaignValidator.runReportId,element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.runReportId).value(runReportId).notNull().integerGreaterThanZero();

        final String message = this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.message, element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.message).value(message).notBlank().notExceedingLengthOf(480);

        final String paramValue = this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.paramValue, element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.paramValue).value(paramValue).notBlank();



        if (this.fromApiJsonHelper.parameterExists(SmsCampaignValidator.submittedOnDateParamName, element)) {
            final LocalDate submittedOnDate = this.fromApiJsonHelper.extractLocalDateNamed(SmsCampaignValidator.submittedOnDateParamName,
                    element);
            baseDataValidator.reset().parameter(SmsCampaignValidator.submittedOnDateParamName).value(submittedOnDate).notNull();
        }



        throwExceptionIfValidationWarningsExist(dataValidationErrors);

    }

    public void validateForUpdate(String json){
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SmsCampaignValidator.supportedParamsForUpdate);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SmsCampaignValidator.RESOURCE_NAME);

        final String campaignName =  this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.campaignName,element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.campaignName).value(campaignName).notBlank().notExceedingLengthOf(100);


        final Long campaignType = this.fromApiJsonHelper.extractLongNamed(SmsCampaignValidator.campaignType,element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.campaignType).value(campaignType).notNull().integerGreaterThanZero();

        if(campaignType.intValue() == SmsCampaignType.SCHEDULE.getValue()){
            final String recurrenceParamName =  this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.recurrenceParamName, element);
            baseDataValidator.reset().parameter(SmsCampaignValidator.recurrenceParamName).value(recurrenceParamName).notBlank();
            validateRecurrenceRule(recurrenceParamName, SmsCampaignValidator.recurrenceParamName, baseDataValidator);
            final String recurrenceStartDate =  this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.recurrenceStartDate, element);
            baseDataValidator.reset().parameter(SmsCampaignValidator.recurrenceStartDate).value(recurrenceStartDate).notBlank();
        }

        final Long runReportId = this.fromApiJsonHelper.extractLongNamed(SmsCampaignValidator.runReportId,element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.runReportId).value(runReportId).notNull().integerGreaterThanZero();

        final String message = this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.message, element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.message).value(message).notBlank().notExceedingLengthOf(480);

        final String paramValue = this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.paramValue, element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.paramValue).value(paramValue).notBlank();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);


    }

    public void validatePreviewMessage(String json){
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SmsCampaignValidator.PREVIEW_REQUEST_DATA_PARAMETERS);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SmsCampaignValidator.RESOURCE_NAME);

        final String paramValue = this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.paramValue, element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.paramValue).value(paramValue).notBlank();

        final String message = this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.message, element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.message).value(message).notBlank().notExceedingLengthOf(480);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);


    }

    public void validateClosedDate(String json){
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SmsCampaignValidator.CLOSE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SmsCampaignValidator.RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final LocalDate closeDate = this.fromApiJsonHelper.extractLocalDateNamed(SmsCampaignValidator.closureDateParamName, element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.closureDateParamName).value(closeDate).notNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
    public void validateActivation(String json){
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SmsCampaignValidator.ACTIVATION_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SmsCampaignValidator.RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final LocalDate activationDate = this.fromApiJsonHelper.extractLocalDateNamed(SmsCampaignValidator.activationDateParamName, element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.activationDateParamName).value(activationDate).notNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
    
    public void validateRecurrenceRule(String recurrence, String param, DataValidatorBuilder baseDataValidator){
        try {
            RRule rule = new RRule(recurrence);
            rule.validate();
        } catch (Exception e) {
            baseDataValidator.reset().parameter(param).failWithCode("invalid.recurrence.rule.exception");
        }
        
    }

    public void ValidateClosure(String json){
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SmsCampaignValidator.CLOSE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SmsCampaignValidator.RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final LocalDate closeDate = this.fromApiJsonHelper.extractLocalDateNamed(SmsCampaignValidator.closureDateParamName, element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.closureDateParamName).value(closeDate).notNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
}
