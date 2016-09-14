package com.finflux.fingerprint.data;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.fingerprint.api.FingerPrintApiConstants;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class FingerPrintDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    /**
     * Validate Create finger print data Request Parameters
     * 
     * @param json
     */
    @Autowired
    public FingerPrintDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    /**
     * Validating all required parameters exists or not
     * 
     * @param entityId
     * @param json
     */

    public void validateForCreate(final Long clientId, final String json) {

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                FingerPrintApiConstants.CREATE_FINGER_PRINT_REQUEST_DATA_PARAMETERS);
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(FingerPrintApiConstants.FINGER_PRINT_RESOURCE_NAME);
        final JsonElement parentElement = this.fromApiJsonHelper.parse(json);

        if (parentElement.isJsonObject()) {
            validateEachJsonObjectForCreate(clientId, parentElement.getAsJsonObject(), baseDataValidator);

        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void validateEachJsonObjectForCreate(final Long clientId, final JsonObject element, final DataValidatorBuilder baseDataValidator) {


        baseDataValidator.reset().parameter(FingerPrintApiConstants.clientIdParamName).value(clientId).notBlank();

        final Integer fingerId = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(FingerPrintApiConstants.fingerIdParamName, element);
        baseDataValidator.reset().parameter(FingerPrintApiConstants.fingerIdParamName).value(fingerId).notNull();
        if(fingerId != null){
        final FingerPrintEntityTypeEnums fingerPrintEntityType = FingerPrintEntityTypeEnums.getEntityType(fingerId);
        if (fingerPrintEntityType == null) { 
            baseDataValidator.reset().parameter(FingerPrintApiConstants.fingerIdParamName).value(fingerId).failWithCode("invalid  value");
        }
        }

        final String fingerPrint = this.fromApiJsonHelper.extractStringNamed(FingerPrintApiConstants.fingerDataParamName, element);
        baseDataValidator.reset().parameter(FingerPrintApiConstants.fingerDataParamName).value(fingerPrint).notNull();
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
}
