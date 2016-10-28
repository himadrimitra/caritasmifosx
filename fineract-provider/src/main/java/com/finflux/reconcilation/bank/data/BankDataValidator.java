/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bank.data;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.reconcilation.ReconciliationApiConstants;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class BankDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public BankDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validate(final JsonCommand command) {
        final String json = command.json();
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, ReconciliationApiConstants.BANK_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ReconciliationApiConstants.BANK_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();

        final String name = this.fromApiJsonHelper.extractStringNamed(ReconciliationApiConstants.nameParamName, element);
        baseDataValidator.reset().parameter(ReconciliationApiConstants.nameParamName).value(name).notNull().notExceedingLengthOf(100);

        final Long glAccount = this.fromApiJsonHelper.extractLongNamed(ReconciliationApiConstants.glAccountParamName, element);
        baseDataValidator.reset().parameter(ReconciliationApiConstants.glAccountParamName).value(glAccount).notNull();
        
        if(command.parameterExists(ReconciliationApiConstants.supportSimplifiedStatement)){
        	final Boolean supportSimplifiedStatement = this.fromApiJsonHelper.extractBooleanNamed(ReconciliationApiConstants.supportSimplifiedStatement, element);
            baseDataValidator.reset().parameter(ReconciliationApiConstants.supportSimplifiedStatement).value(supportSimplifiedStatement).validateForBooleanValue();
        }
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            //
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
    }

}
